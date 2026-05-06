package logo.lsp.analysis;

public class LogoAnalyzer {

    //Bu layer “kod ne anlama geliyor?” sorusuyla ilgilenir.
    //analysis/
    //├── LogoAnalyzer.java
    //└── LogoAnalysisResult.java
    //LogoAnalyzer
    //En önemli analysis class.
    //Görevi:
    //tokenları al
    //procedure declarationları bul
    //variable declarationları bul
    //reference kullanımlarını bul
    //diagnostics üret
    //Örnek:
    //to square :size
    //  forward :size
    //end
    //
    //square 100
    //Analyzer bunu şöyle anlar:
    //procedure declarations:
    //square -> line 0, column 3
    //
    //variable declarations:
    //size -> line 0, column 10
    //
    //references:
    //:size -> declaration size
    //square -> declaration square
    //
    //diagnostics:
    //empty
}
