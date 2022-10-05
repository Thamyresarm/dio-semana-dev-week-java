package me.dio.sacola.service;

import me.dio.sacola.model.*;
import me.dio.sacola.resource.dto.ItemDto;

public interface SacolaService {

    Item incluirItemSacola(ItemDto ItemDto);
    Sacola verSacola(Long id);
    Sacola fecharSacola(Long id, int formaPagamento);
    Item excluirItemSacola(Long itemId);
    
}
