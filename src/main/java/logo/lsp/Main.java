package logo.lsp;

import logo.lsp.server.LogoLanguageServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;

/**
 * Creates LogoLanguageServer and LSP4J launcher objects.
 * Starts stdin and stdout connection.
 * Here logo analysis will not be executed.
 */
public class Main {

    private Main() {

    }

    public static void main(String[] args) {
        LogoLanguageServer server = new LogoLanguageServer();
        Launcher<LanguageClient> launcher = Launcher.createLauncher(server, LanguageClient.class, System.in, System.out);

        server.connect(launcher.getRemoteProxy());
        launcher.startListening();
    }
}
