class Jone9 {
  private int debugMode;

  public Jone9(){

  }

  private Jone9(String debug){
    this();

    try {
      this.debugMode = Integer.parseInt(debug);
    } catch (NumberFormatException e) {
      System.out.println("[-] if you want to use DEBUG MODE, please set argv to 1");      
      System.exit(1);
    }

    if (this.debugMode == 1){
      System.out.println("[*] DEBUG MODE");
    }
  }

  public static void main(String argv[]){
    if (argv.length >= 1){
      new Jone9(argv[0]);
    } else {
      new Jone9();
    }
  }
}