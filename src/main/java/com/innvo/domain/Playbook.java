package com.innvo.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.innvo.json.StringJsonUserType;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Playbook.
 */
@Entity
@Table(name = "playbook")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "playbook")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = StringJsonUserType.class)})
public class Playbook implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull
    @Size(max = 20)
    @Column(name = "nameshort", length = 20, nullable = false)
    private String nameshort;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    //@Size(max = 4000)
    //@Column(name = "parameters", length = 4000)
    @Column(name = "parameters")
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    @Type(type = "StringJsonObject")
    private String parameters;

    @Size(max = 255)
    @Column(name = "jobfilename", length = 255)
    private String jobfilename;

    @Size(max = 1000)
    @Column(name = "commandlinescript", length = 1000)
    private String commandlinescript;

    @NotNull
    @Size(max = 25)
    @Column(name = "status", length = 25, nullable = false)
    private String status;

    @NotNull
    @Size(max = 50)
    @Column(name = "lastmodifiedby", length = 50, nullable = false)
    private String lastmodifiedby;

    @NotNull
    @Column(name = "lastmodifieddatetime", nullable = false)
    private ZonedDateTime lastmodifieddatetime;

    @NotNull
    @Size(max = 25)
    @Column(name = "domain", length = 25, nullable = false)
    private String domain;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Playbook name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameshort() {
        return nameshort;
    }

    public Playbook nameshort(String nameshort) {
        this.nameshort = nameshort;
        return this;
    }

    public void setNameshort(String nameshort) {
        this.nameshort = nameshort;
    }

    public String getDescription() {
        return description;
    }

    public Playbook description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParameters() {
        return parameters;
    }

    public Playbook parameters(String parameters) {
        this.parameters = parameters;
        return this;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getJobfilename() {
        return jobfilename;
    }

    public Playbook jobfilename(String jobfilename) {
        this.jobfilename = jobfilename;
        return this;
    }

    public void setJobfilename(String jobfilename) {
        this.jobfilename = jobfilename;
    }

    public String getCommandlinescript() {
        return commandlinescript;
    }

    public Playbook commandlinescript(String commandlinescript) {
        this.commandlinescript = commandlinescript;
        return this;
    }

    public void setCommandlinescript(String commandlinescript) {
        this.commandlinescript = commandlinescript;
    }

    public String getStatus() {
        return status;
    }

    public Playbook status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastmodifiedby() {
        return lastmodifiedby;
    }

    public Playbook lastmodifiedby(String lastmodifiedby) {
        this.lastmodifiedby = lastmodifiedby;
        return this;
    }

    public void setLastmodifiedby(String lastmodifiedby) {
        this.lastmodifiedby = lastmodifiedby;
    }

    public ZonedDateTime getLastmodifieddatetime() {
        return lastmodifieddatetime;
    }

    public Playbook lastmodifieddatetime(ZonedDateTime lastmodifieddatetime) {
        this.lastmodifieddatetime = lastmodifieddatetime;
        return this;
    }

    public void setLastmodifieddatetime(ZonedDateTime lastmodifieddatetime) {
        this.lastmodifieddatetime = lastmodifieddatetime;
    }

    public String getDomain() {
        return domain;
    }

    public Playbook domain(String domain) {
        this.domain = domain;
        return this;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Playbook playbook = (Playbook) o;
        if(playbook.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, playbook.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Playbook{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", nameshort='" + nameshort + "'" +
            ", description='" + description + "'" +
            ", parameters='" + parameters + "'" +
            ", jobfilename='" + jobfilename + "'" +
            ", commandlinescript='" + commandlinescript + "'" +
            ", status='" + status + "'" +
            ", lastmodifiedby='" + lastmodifiedby + "'" +
            ", lastmodifieddatetime='" + lastmodifieddatetime + "'" +
            ", domain='" + domain + "'" +
            '}';
    }
}
