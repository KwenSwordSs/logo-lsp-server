package logo.lsp.server;

public class LogoWorkspaceService {

//LogoWorkspaceService neden yok/olmamalı?
//Aslında LSP4J’de LanguageServer interface’i senden iki servis ister:
//getTextDocumentService()
//getWorkspaceService()
//Yani teknik olarak LogoWorkspaceService lazım.
//Ama bizim proje workspace feature kullanmayacak. O yüzden bu class çok minimal olacak.
//server/
//├── LogoLanguageServer.java
//├── LogoTextDocumentService.java
//└── LogoWorkspaceService.java
//LogoWorkspaceService şimdilik sadece interface’i satisfy etmek için var.
}
