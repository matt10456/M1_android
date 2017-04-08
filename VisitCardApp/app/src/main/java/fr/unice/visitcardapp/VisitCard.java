package fr.unice.visitcardapp;

public class VisitCard {
    private String firstName;
    private String surname;
    private String phoneNumber;
    private String job;
    private String email;
    private String website;

    public VisitCard(String firstName, String surname, String phoneNumber, String job, String email, String website) {
        this.firstName = firstName;
        this. surname = surname;
        this.phoneNumber = phoneNumber;
        this.job = job;
        this.email = email;
        this.website = website;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

}
