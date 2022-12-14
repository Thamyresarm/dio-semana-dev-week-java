package me.dio.sacola.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.dio.sacola.enumeration.FormaPagamento;
import me.dio.sacola.model.Item;
import me.dio.sacola.model.Restaurante;
import me.dio.sacola.model.Sacola;
import me.dio.sacola.repository.ItemRepository;
import me.dio.sacola.repository.ProdutoRepository;
import me.dio.sacola.repository.SacolaRepository;
import me.dio.sacola.resource.dto.ItemDto;

@Service
@RequiredArgsConstructor
public class SacolaServiceImpl implements SacolaService {

    private final SacolaRepository sacolaRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemRepository itemRepository;

    @Override
    public Item incluirItemSacola(ItemDto itemDto) {
        
        Sacola sacola = verSacola(itemDto.getSacolaId());

        if (sacola.isFechada()) {
            throw new RuntimeException("Essa sacola está fechada!");
        }

        Item itemParaSerInserido = Item.builder()
            .quantidade(itemDto.getQuantidade()) 
            .sacola(sacola)
            .produto(produtoRepository.findById(itemDto.getProdutoId()).orElseThrow(
                () -> {
                    throw new RuntimeException("Esse produto não existe!"); 
                }
            ))
        .build();

        List<Item> itensDaSacola = sacola.getItens();
        
        if(itensDaSacola.isEmpty()){
            itensDaSacola.add(itemParaSerInserido);
        } else {
            Restaurante restauranteAtual = itensDaSacola.get(0).getProduto().getRestaurante();
            Restaurante restauranteDoItemParaAdicionar = itemParaSerInserido.getProduto().getRestaurante();
            if (restauranteAtual.equals(restauranteDoItemParaAdicionar)){
                itensDaSacola.add(itemParaSerInserido);
            } else {
                throw new RuntimeException("Não é possível adicionar produtos de restaurante diferente!"); 
            }            
        }

        List<Double> valorDosItens = new ArrayList<>();

        for(Item itemDaSacola: itensDaSacola){
            double valorTotalItem = itemDaSacola.getProduto().getValorUnitario() * itemDaSacola.getQuantidade();
            valorDosItens.add(valorTotalItem);
        }

      /*   Double valorTotalSacola = 0.0;

       for(Double valorDeCadaItem: valorDosItens){
            valorTotalSacola+=valorDeCadaItem;
        } */

        double valorTotalSacola = valorDosItens.stream().mapToDouble(valorTotalDeCadaItem -> valorTotalDeCadaItem).sum();

        sacola.setValorTotal(valorTotalSacola);

        sacolaRepository.save(sacola);

        return itemParaSerInserido;
    }

    @Override
    public Sacola verSacola(Long id) {
       return sacolaRepository.findById(id).orElseThrow(
            () -> {
                throw new RuntimeException("Essa sacola não existe!");
            }
       );
    }

    @Override
    public Sacola fecharSacola(Long id, int numeroFormaPagamento) {
        Sacola sacola = verSacola(id);
            if(sacola.getItens().isEmpty()){
                throw new RuntimeException("Inclua itens na sacola!");
            }

        FormaPagamento formaPagamento = numeroFormaPagamento == 0 ? FormaPagamento.DINHEIRO : FormaPagamento.MAQUINETA;

        sacola.setFormaPagamento(formaPagamento);
        sacola.setFechada(true);

       return sacolaRepository.save(sacola);
    }

    @Override
    public Item excluirItemSacola(Long itemId) {

        Item item = itemRepository.findById(itemId).orElseThrow(
            () -> {
                throw new RuntimeException("Esse item não existe!");
            }
       );

        Sacola sacola = verSacola(item.getSacola().getId());

        if (sacola.isFechada()) {
            throw new RuntimeException("Essa sacola está fechada, não é possível excluir o item!");
        }

        List<Item> itensDaSacola = sacola.getItens();
        
        itensDaSacola.remove(item);
        sacolaRepository.save(sacola);

        return null;
    }
    
}
