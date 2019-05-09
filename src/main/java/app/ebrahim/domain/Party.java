package app.ebrahim.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

public class Party implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private Long id;
    @JsonProperty(required = true)
    private String username;
    @JsonProperty(required = true)
    private String email;
    @JsonProperty(required = true)
    private String createPartyDate;

    public Party() {
    }

    public Party(Long id, String username, String email, String createPartyDate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createPartyDate = createPartyDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreatePartyDate() {
        return createPartyDate;
    }

    public void setCreatePartyDate(String createPartyDate) {
        this.createPartyDate = createPartyDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Party party = (Party) o;
        return id == party.id &&
                Objects.equals(username, party.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return new StringBuilder("Party{")
                .append("id=").append(id)
                .append(", username='").append(username).append("\'")
                .append(", email='").append(email).append("\'")
                .append(", createPartyDate='").append(createPartyDate)
                .append("}").toString();
    }
}
