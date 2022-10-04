package me.dio.sacola.model;

import javax.persistence.*;
import lombok.*;

@AllArgsConstructor
@Builder
@Data
@Embeddable
@NoArgsConstructor
public class Endereco {
    private String cep;
    private String complemento;
}
