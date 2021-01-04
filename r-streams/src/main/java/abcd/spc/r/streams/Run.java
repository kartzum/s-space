package abcd.spc.r.streams;

public class Run {
    private String key;
    private String type;
    private String responseKey;
    private String body;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResponseKey() {
        return responseKey;
    }

    public void setResponseKey(String responseKey) {
        this.responseKey = responseKey;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Run() {
    }

    public Run(String key, String type, String responseKey, String body) {
        this.key = key;
        this.type = type;
        this.responseKey = responseKey;
        this.body = body;
    }
}
