package com.aws.practice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ServiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private String jobId;
    private String instanceId;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "files", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "file", nullable = false)
    private List<String> files;

    public ServiceRecord(String name,String type ,String jobId, String instanceId) {
        this.name = name;
        this.type = type;
        this.jobId = jobId;
        this.instanceId = instanceId;
    }
}