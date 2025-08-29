package com.example.sgb.repository;

import com.example.sgb.model.EmprestimoView;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class EmprestimoViewSpecs {
    public static Specification<EmprestimoView> comCodUsuario(Integer codUsuario) {
        return (root, query, cb) -> codUsuario == null ? null : cb.equal(root.get("codUsuario"), codUsuario);
    }
    public static Specification<EmprestimoView> comEmAtraso(Boolean emAtraso) {
        return (root, query, cb) -> emAtraso == null ? null : cb.equal(root.get("emAtraso"), emAtraso);
    }
    public static Specification<EmprestimoView> comCodLivro(Integer codLivro) {
        return (root, query, cb) -> codLivro == null ? null : cb.equal(root.get("codLivro"), codLivro);
    }
    public static Specification<EmprestimoView> comEntregue(Boolean entregue) {
        return (root, query, cb) -> {
            if (entregue == null) return null;
            if (entregue) {
                return cb.isNotNull(root.get("dataDeEntrega"));
            } else {
                return cb.isNull(root.get("dataDeEntrega"));
            }
        };
    }

}
