package com.github.darkroom.cases.repository;

import com.github.darkroom.classifiers.repository.ClassifierEntity;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "cases")
public class CaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "classifier_id", referencedColumnName = "id")
    private ClassifierEntity classifier;

    @Column(name = "file")
    private String file;

    @Column(name = "base")
    private String base;

    @Column(name = "delta")
    private String delta;

    @Column(name = "diff_percent")
    private Integer diffPercent;

    @Column(name = "timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDiffPercent() {
        return diffPercent;
    }

    public void setDiffPercent(Integer diffPercent) {
        this.diffPercent = diffPercent;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getDelta() {
        return delta;
    }

    public void setDelta(String delta) {
        this.delta = delta;
    }

    public ClassifierEntity getClassifier() {
        return classifier;
    }

    public void setClassifier(ClassifierEntity classifier) {
        this.classifier = classifier;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
