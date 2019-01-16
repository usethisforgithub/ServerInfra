public class Account {
    private String name;
    private String passwordHash;
    private boolean loggedIn;

    public Account(String n, String p){
        name = n;
        passwordHash = p;
        loggedIn = false;
    }

    public int logIn(String passHash){
        boolean success = passHash.equals(passwordHash);

        if(success && loggedIn == false){
            loggedIn = true;//successful login
            return 0;
        }else if(success && loggedIn == true){
            return 2;//fishy flag, shows possible hacked client
        }else{
            return 1;//wrong password
        }

    }

    public int LogOut(String passHash){
        boolean success = passHash.equals(passwordHash);

        if(success && loggedIn == true){
            loggedIn = true;//successful login
            return 0;
        }else if(success && loggedIn == false){
            return 2;//fishy flag, shows possible hacked client
        }else{
            return 1;//fishy flag, shows possible hacked client
        }
    }

    public boolean changePassword(String oldPassHash, String newPassHash){
        boolean success = oldPassHash.equals(passwordHash);
        if(success){
            passwordHash = newPassHash;
        }
        return success;
    }

    public String getName(){
        return name;
    }
}
