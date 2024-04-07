package ac.mz.projecto01.ui;
public class model {
    private String associatedText;
    private String imageUri1;
    private String imageUri2;
    private String entryId;
    private String expirationDate;

    public model() {
        // Construtor vazio necessário para Firebase
    }

    public model(String associatedText, String imageUri1, String imageUri2, String expirationDate) {
        this.associatedText = associatedText;
        this.imageUri1 = imageUri1;
        this.imageUri2 = imageUri2;
        this.expirationDate = expirationDate;;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getAssociatedText() {
        return associatedText;
    }

    public void setAssociatedText(String associatedText) {
        this.associatedText = associatedText;
    }

    public String getImageUri1() {
        return imageUri1;
    }

    public void setImageUri1(String imageUri1) {
        this.imageUri1 = imageUri1;
    }

    public String getImageUri2() {
        return imageUri2;
    }

    public void setImageUri2(String imageUri2) {
        this.imageUri2 = imageUri2;
    }
}

