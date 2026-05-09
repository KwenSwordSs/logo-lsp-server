package logo.lsp.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.util.List;

import java.util.concurrent.CompletableFuture;

import logo.lsp.features.DefinitionFeature;
import logo.lsp.features.DiagnosticsFeature;
import logo.lsp.features.SemanticTokensFeature;
import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;

/**
 * Handles text document events sent by the LSP client.
 */
public final class LogoTextDocumentService implements TextDocumentService {

    private final Map<String, String> documents = new ConcurrentHashMap<>();
    
    private final SemanticTokensFeature semanticTokensFeature = new SemanticTokensFeature();
    private final DefinitionFeature definitionFeature = new DefinitionFeature();
    private final DiagnosticsFeature diagnosticsFeature = new DiagnosticsFeature();

    private LanguageClient client;

    public void connect(LanguageClient client) {
        this.client = client;
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String text = params.getTextDocument().getText();

        documents.put(uri, text);
        publishDiagnostics(uri, text);
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        List<TextDocumentContentChangeEvent> changes = params.getContentChanges();

        if (!changes.isEmpty()) {
            String text = changes.get(changes.size() - 1).getText();
            documents.put(uri, text);
            publishDiagnostics(uri, text);
        }
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        documents.remove(uri);

        if (client != null) {
            client.publishDiagnostics(new PublishDiagnosticsParams(uri, List.of()));
        }
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        // Diagnostics are updated on open and change.
    }

    @Override
    public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
        String uri = params.getTextDocument().getUri();
        String text = documents.getOrDefault(uri, "");

        SemanticTokens tokens = semanticTokensFeature.createSemanticTokens(text);
        return CompletableFuture.completedFuture(tokens);
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends org.eclipse.lsp4j.LocationLink>>> definition(
        DefinitionParams params
    ) {
        String uri = params.getTextDocument().getUri();
        String text = documents.getOrDefault(uri, "");

        List<Location> locations = definitionFeature.findDefinition(uri, text, params.getPosition());
        return CompletableFuture.completedFuture(Either.forLeft(locations));
    }

    private void publishDiagnostics(String uri, String text) {
        if (client == null) {
            return;
        }

        List<Diagnostic> diagnostics = diagnosticsFeature.createDiagnostics(text);
        client.publishDiagnostics(new PublishDiagnosticsParams(uri, diagnostics));
    }
}