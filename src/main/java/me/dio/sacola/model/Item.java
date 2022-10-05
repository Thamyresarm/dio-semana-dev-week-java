package me.dio.sacola.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@AllArgsConstructor
@Builder
@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private Produto produto;
    private int quantidade;
    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private Sacola sacola;
    
}
