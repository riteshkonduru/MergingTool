package deloitte;

public class ProfileElements {

    private String name;
    private String enabled;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEnabled() {
        return enabled;
    }
    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "Profile:: Name=" + this.name  + " Enabled=" + this.enabled ;
    }
    
}
