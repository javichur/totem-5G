package adgarcis.com.adgarcisacceso;

public class DrawerItem {
    private String name;
    private int iconId;

    public DrawerItem(String name, int iconId) {
        this.name = name;
        this.iconId = iconId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconId() {
        return iconId;
    }
}
