package abcd.spc.l.streams.sr.jt;

public class Block {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String id;
    String name;

    public Block() {
    }

    public Block(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
