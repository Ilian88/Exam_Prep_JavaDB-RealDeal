package softuni.exam.models;

import softuni.exam.models.enums.Rating;

import javax.persistence.*;

@Entity
@Table(name = "sellers")
public class Seller extends BaseEntity{

    private String firstName;

    private String lastName;

    private String email;

    private Rating rating;

    private String town;

    public Seller() {
    }

    public String getFirstName() {
        return firstName;
    }

    @Column
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Column
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Enumerated(EnumType.STRING)
    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    @Column
    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }
}
