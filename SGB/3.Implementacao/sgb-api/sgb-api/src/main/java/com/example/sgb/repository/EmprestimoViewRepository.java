package com.example.sgb.repository;

import com.example.sgb.model.EmprestimoView;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import java.util.List;

public interface EmprestimoViewRepository extends Repository<EmprestimoView, Integer>, JpaSpecificationExecutor<EmprestimoView> {
    List<EmprestimoView> findAll();

    List<EmprestimoView> findByCodUsuario(Integer codUsuario);

    List<EmprestimoView> findByEmAtraso(Boolean emAtraso);

    List<EmprestimoView> findByCodUsuarioAndEmAtraso(Integer codUsuario, Boolean emAtraso);

    List<EmprestimoView> findByCodLivro(Integer codLivro);
}
