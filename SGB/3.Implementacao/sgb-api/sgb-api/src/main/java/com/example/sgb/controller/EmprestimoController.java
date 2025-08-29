package com.example.sgb.controller;

import com.example.sgb.model.Emprestimo;
import com.example.sgb.model.EmprestimoView;
import com.example.sgb.model.Livro;
import com.example.sgb.model.LivroDisponibilidade;
import com.example.sgb.model.Usuario;
import com.example.sgb.repository.EmprestimoRepository;
import com.example.sgb.repository.EmprestimoViewRepository;
import com.example.sgb.repository.LivroRepository;
import com.example.sgb.repository.LivroDisponibilidadeRepository;
import com.example.sgb.repository.UsuarioRepository;
import com.example.sgb.repository.EmprestimoViewSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {
    @Autowired
    private EmprestimoViewRepository emprestimoViewRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EmprestimoRepository emprestimoRepository;
    @Autowired
    private LivroRepository livroRepository;
    @Autowired
    private LivroDisponibilidadeRepository livroDisponibilidadeRepository;

    @GetMapping
    public List<EmprestimoView> listarTodos(
            Authentication authentication,
            @RequestParam(value = "em_atraso", required = false) Boolean emAtraso,
            @RequestParam(value = "usuario", required = false) Integer usuarioId,
            @RequestParam(value = "cod_livro", required = false) Integer codLivro,
            @RequestParam(value = "entregue", required = false) Boolean entregue
    ) {
        Specification<EmprestimoView> spec;
        if (hasRole(authentication, "ADMIN") || hasRole(authentication, "BIBLIOTECARIO")) {
            spec = EmprestimoViewSpecs.comCodUsuario(usuarioId)
                    .and(EmprestimoViewSpecs.comEmAtraso(emAtraso))
                    .and(EmprestimoViewSpecs.comCodLivro(codLivro))
                    .and(EmprestimoViewSpecs.comEntregue(entregue));
        } else {
            String email = authentication.getName();
            Integer codUsuario = usuarioRepository.findByEmail(email)
                    .map(u -> u.getCodigologin())
                    .orElse(-1);
            spec = EmprestimoViewSpecs.comCodUsuario(codUsuario)
                    .and(EmprestimoViewSpecs.comEmAtraso(emAtraso))
                    .and(EmprestimoViewSpecs.comCodLivro(codLivro))
                    .and(EmprestimoViewSpecs.comEntregue(entregue));
        }
        return emprestimoViewRepository.findAll(spec);
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_" + role));
    }

    @PostMapping
    public ResponseEntity<?> criarEmprestimo(@RequestBody Emprestimo emprestimo, Authentication authentication) {
        boolean isUsuario = hasRole(authentication, "USUARIO");
        String email = authentication.getName();

        // Validação: data de retirada não pode ser futura
        if (emprestimo.getDataderetirada() != null && emprestimo.getDataderetirada().isAfter(LocalDate.now())) {
            return ResponseEntity.badRequest().body("A data de retirada não pode ser posterior à data atual.");
        }

        // Validação: data de entrega não pode ser anterior à data de retirada
        if (emprestimo.getData_prevista() != null && emprestimo.getDataderetirada() != null) {
            if (emprestimo.getData_prevista().isBefore(emprestimo.getDataderetirada())) {
                return ResponseEntity.badRequest().body("A data prevista não pode ser anterior à data de retirada.");
            }
        }

        // Validação: livro deve estar disponível (consultando view)
        if (emprestimo.getLivro() == null || emprestimo.getLivro().getCodigolivro() == null) {
            return ResponseEntity.badRequest().body("Livro não informado.");
        }
        Integer codLivro = emprestimo.getLivro().getCodigolivro();
        Optional<LivroDisponibilidade> livroDispOpt = livroDisponibilidadeRepository.findById(codLivro);
        if (livroDispOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Livro não encontrado.");
        }
        LivroDisponibilidade livroDisp = livroDispOpt.get();
        if (!"DISPONIVEL".equalsIgnoreCase(livroDisp.getDisponibilidade())) {
            return ResponseEntity.badRequest().body("O livro não está disponível para empréstimo.");
        }
        // Busca o livro real para associar ao empréstimo
        Optional<Livro> livroOpt = livroRepository.findById(codLivro);
        if (livroOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Livro não encontrado.");
        }
        Livro livro = livroOpt.get();

        // Validação: se for USUARIO, só pode criar para si mesmo
        Usuario usuario = null;
        if (emprestimo.getUsuario() == null || emprestimo.getUsuario().getCodigologin() == null) {
            return ResponseEntity.badRequest().body("Usuário não informado.");
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(emprestimo.getUsuario().getCodigologin());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário não encontrado.");
        }
        usuario = usuarioOpt.get();
        if (isUsuario) {
            Integer codUsuarioLogado = usuarioRepository.findByEmail(email).map(Usuario::getCodigologin).orElse(-1);
            if (!usuario.getCodigologin().equals(codUsuarioLogado)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Usuário só pode criar empréstimo para si mesmo.");
            }
        }

        // Validação: não pode ter empréstimo em atraso
        Integer codUsuarioParaValidar = usuario.getCodigologin();
        boolean temAtraso = emprestimoViewRepository.findByCodUsuarioAndEmAtraso(codUsuarioParaValidar, true)
                .stream().findAny().isPresent();
        if (temAtraso) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Usuário não pode criar empréstimo com empréstimos em atraso.");
        }

        // Cria o empréstimo
        emprestimo.setLivro(livro);
        emprestimo.setUsuario(usuario);
        Emprestimo salvo = emprestimoRepository.save(emprestimo);

        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<?> atualizarEmprestimo(@PathVariable Integer id, @RequestBody Emprestimo emprestimo,
            Authentication authentication) {
        Optional<Emprestimo> emprestimoOpt = emprestimoRepository.findById(id);
        if (emprestimoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empréstimo não encontrado.");
        }
        Emprestimo existente = emprestimoOpt.get();
        LocalDate novaDataEntrega = emprestimo.getDatadeentrega();

        // Não permite editar se já existe outro empréstimo mais recente para o mesmo livro
        boolean existeMaisRecente = emprestimoRepository
            .findByUsuario_Codigologin(existente.getUsuario().getCodigologin())
            .stream()
            .anyMatch(e -> !e.getCodigoemprestimo().equals(id) &&
                          e.getLivro().getCodigolivro().equals(existente.getLivro().getCodigolivro()) &&
                          e.getDataderetirada() != null &&
                          e.getDataderetirada().isAfter(existente.getDataderetirada()));
        if (existeMaisRecente) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Não é possível editar: já existe outro empréstimo mais recente para este livro.");
        }

        // Validação: data de entrega não pode ser anterior à data de retirada
        if (novaDataEntrega != null && existente.getDataderetirada() != null) {
            if (novaDataEntrega.isBefore(existente.getDataderetirada())) {
                return ResponseEntity.badRequest().body("A data de entrega não pode ser anterior à data de retirada.");
            }
        }

        // Atualiza apenas o campo permitido
        existente.setDatadeentrega(novaDataEntrega);

        Emprestimo salvo = emprestimoRepository.save(existente);
        return ResponseEntity.ok(salvo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTECARIO')")
    public ResponseEntity<?> deletarEmprestimo(@PathVariable Integer id) {
        Optional<Emprestimo> emprestimoOpt = emprestimoRepository.findById(id);
        if (emprestimoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        emprestimoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
