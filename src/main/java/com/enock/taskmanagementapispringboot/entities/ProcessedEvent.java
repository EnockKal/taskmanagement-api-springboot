package com.enock.taskmanagementapispringboot.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "processed_events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ProcessedEvent {
    @Id
    private String eventId;
}
