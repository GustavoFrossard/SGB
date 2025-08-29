import { useEffect, useState } from 'react';

const GENEROS = [
  { value: '', label: 'Todos os Gêneros' },
  { value: 'ACAO_E_AVENTURA', label: 'Ação e Aventura' },
  { value: 'BIBLIOGRAFIA', label: 'Bibliografia' },
  { value: 'COMEDIA', label: 'Comédia' },
  { value: 'FANTASIA', label: 'Fantasia' },
  { value: 'FICCAO_CIENTIFICA', label: 'Ficção Científica' },
  { value: 'ROMANCE', label: 'Romance' },
  { value: 'SUSPENSE', label: 'Suspense' },
];
const DISPONIBILIDADES = [
  { value: '', label: 'Todas' },
  { value: 'DISPONIVEL', label: 'Disponível' },
  { value: 'INDISPONIVEL', label: 'Indisponível' },
];

export default function LivrosPage({ user, isAdminOrBiblio, onLogout }) {
  const [livros, setLivros] = useState([]);
  const [livrosFiltrados, setLivrosFiltrados] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [filtros, setFiltros] = useState({ nome: '', autor: '', genero: '', disponibilidade: 'DISPONIVEL' });
  const [showCreate, setShowCreate] = useState(false);
  const [showLivro, setShowLivro] = useState(null);
  const [livroCriadoMsg, setLivroCriadoMsg] = useState('');

  useEffect(() => {
    buscarLivros();
    // eslint-disable-next-line
  }, []);

  function buscarLivros() {
    setLoading(true);
    setError('');
    const params = [];
    if (filtros.genero) params.push(`genero=${filtros.genero}`);
    if (filtros.disponibilidade) params.push(`disponibilidade=${filtros.disponibilidade}`);
    const query = params.length ? `?${params.join('&')}` : '';
    fetch(`http://localhost:8080/livros${query}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`,
      },
    })
      .then(res => res.json())
      .then(data => {
        setLivros(Array.isArray(data) ? data : []);
      })
      .catch(() => setError('Erro ao buscar livros.'))
      .finally(() => setLoading(false));
  }

  function handleFiltroChange(e) {
    const { name, value } = e.target;
    setFiltros(f => ({ ...f, [name]: value }));
  }

  useEffect(() => {
    let filtrados = livros;
    if (filtros.nome) {
      filtrados = filtrados.filter(livro => livro.nome.toLowerCase().includes(filtros.nome.toLowerCase()));
    }
    if (filtros.autor) {
      filtrados = filtrados.filter(livro => livro.autor.toLowerCase().includes(filtros.autor.toLowerCase()));
    }
    setLivrosFiltrados(filtrados);
  }, [livros, filtros.nome, filtros.autor]);

  useEffect(() => {
    buscarLivros();
    // eslint-disable-next-line
  }, [filtros.genero, filtros.disponibilidade]);

  // --- FLUXO DE CRIAÇÃO: mensagem de sucesso ANTES de abrir modal de edição ---
  function handleLivroCriado(livroCriado) {
    buscarLivros();
    setShowCreate(false);
    setShowLivro({ ...livroCriado, _showSuccess: true });
  }

  function formatGenero(genero) {
    const map = {
      ACAO_E_AVENTURA: 'Ação e Aventura',
      BIBLIOGRAFIA: 'Bibliografia',
      COMEDIA: 'Comédia',
      FANTASIA: 'Fantasia',
      FICCAO_CIENTIFICA: 'Ficção Científica',
      ROMANCE: 'Romance',
      SUSPENSE: 'Suspense',
    };
    return map[genero] || genero;
  }

  function LivroCard({ livro, onClick, isClickable }) {
    return (
      <div
        className={`sgb-livro-card${isClickable ? ' sgb-livro-card-clickable' : ''}`}
        onClick={onClick}
        tabIndex={isClickable ? 0 : undefined}
        style={isClickable ? { cursor: 'pointer' } : {}}
      >
        <div className="sgb-livro-codigo">id: {livro.codigolivro}</div>
        <div className="sgb-livro-nome">{livro.nome}</div>
        <div className="sgb-livro-autor">Autor: {livro.autor}</div>
        <div className="sgb-livro-genero">Gênero: {formatGenero(livro.genero)}</div>
        <div className={`sgb-livro-status ${livro.disponibilidade === 'DISPONIVEL' ? 'disponivel' : 'indisponivel'}`}> 
          {livro.disponibilidade === 'DISPONIVEL' ? 'Disponível' : 'Indisponível'}
        </div>
      </div>
    );
  }

  // --- CriarLivroForm: Modal de criação de livro ---
  function CriarLivroForm({ onClose, onCreated }) {
    const [nome, setNome] = useState('');
    const [autor, setAutor] = useState('');
    const [genero, setGenero] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    function handleSubmit(e) {
      e.preventDefault();
      setError('');
      if (!nome.trim() || !autor.trim() || !genero) {
        setError('Preencha todos os campos.');
        return;
      }
      setLoading(true);
      fetch('http://localhost:8080/livros', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify({ nome, autor, genero }),
      })
        .then(res => res.json())
        .then(data => {
          if (data && data.codigolivro) {
            data.disponibilidade = 'DISPONIVEL'; // Assume novo livro sempre começa disponível
            onCreated(data);
          } else {
            setError('Erro ao criar livro.');
          }
        })
        .catch(() => setError('Erro ao criar livro.'))
        .finally(() => setLoading(false));
    }

    return (
      <div className="sgb-modal-bg">
        <form className="sgb-modal-form" onSubmit={handleSubmit}>
          <button className="sgb-modal-close-x" onClick={onClose} type="button" title="Fechar">×</button>
          <h3>Novo Livro</h3>
          <input
            type="text"
            placeholder="Nome do livro"
            value={nome}
            onChange={e => setNome(e.target.value)}
            required
          />
          <input
            type="text"
            placeholder="Autor"
            value={autor}
            onChange={e => setAutor(e.target.value)}
            required
          />
          <select value={genero} onChange={e => setGenero(e.target.value)} required>
            <option value="">Selecione o gênero</option>
            {GENEROS.filter(g => g.value).map(g => (
              <option key={g.value} value={g.value}>{g.label}</option>
            ))}
          </select>
          {error && <div className="sgb-error">{error}</div>}
          <div className="sgb-modal-actions">
            <button className="sgb-btn-cancelar" type="button" onClick={onClose}>Cancelar</button>
            <button type="submit" disabled={loading}>{loading ? 'Salvando...' : 'Salvar'}</button>
          </div>
        </form>
      </div>
    );
  }

  // --- LivroModal: Modal de visualização/edição/exclusão de livro ---
  function LivroModal({ livro, onClose, onUpdated, isAdminOrBiblio }) {
    const [editMode, setEditMode] = useState(false);
    const [form, setForm] = useState({ nome: livro.nome, autor: livro.autor, genero: livro.genero });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [deleted, setDeleted] = useState(false);
    const [success, setSuccess] = useState(livro._showSuccess ? 'Livro criado com sucesso!' : '');
    const [showEmprestimoModal, setShowEmprestimoModal] = useState(false);

    useEffect(() => { setForm({ nome: livro.nome, autor: livro.autor, genero: livro.genero }); }, [livro]);

    function handleChange(e) {
      setForm(f => ({ ...f, [e.target.name]: e.target.value }));
    }

    function handleSave(e) {
      e.preventDefault();
      setError('');
      setLoading(true);
      fetch(`http://localhost:8080/livros/${livro.codigolivro}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify(form),
      })
        .then(res => res.json())
        .then(data => {
          setEditMode(false);
          setSuccess('Livro atualizado com sucesso!');
          setTimeout(() => {
            setSuccess('');
            //onUpdated && onUpdated();
          }, 2000);
        })
        .catch(() => setError('Erro ao atualizar livro.'))
        .finally(() => setLoading(false));
    }

    function handleDelete() {
      if (!window.confirm('Tem certeza que deseja excluir este livro?')) return;
      setLoading(true);
      fetch(`http://localhost:8080/livros/${livro.codigolivro}`, {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
      })
        .then(res => {
          if (res.ok) {
            setDeleted(true);
            setSuccess('Livro excluído com sucesso!');
            setTimeout(() => {
              setSuccess('');
              onClose && onClose();
              onUpdated && onUpdated();
            }, 2000);
          } else {
            setError('Erro ao excluir livro.');
          }
        })
        .catch(() => setError('Erro ao excluir livro.'))
        .finally(() => setLoading(false));
    }

    function handleEmprestar() {
        setShowEmprestimoModal(true);
    }

    useEffect(() => {
      if (success) {
        const t = setTimeout(() => setSuccess(''), 2000);
        return () => clearTimeout(t);
      }
    }, [success]);

    return (
      <>
        {!showEmprestimoModal && (
          <div className="sgb-modal-bg">
            <form className="sgb-modal-form" onSubmit={handleSave}>
              <button className="sgb-modal-close-x" onClick={onClose} type="button" title="Fechar">×</button>
              <h3>Livro #{livro.codigolivro}</h3>
              <label style={{fontWeight:'500',fontSize:'0.97rem',color:'#64748b',marginBottom:'2px',marginTop:'4px',display:'block',lineHeight:'1.1'}}>Nome</label>
              <input
                type="text"
                name="nome"
                value={form.nome}
                onChange={handleChange}
                disabled={!editMode || deleted}
                required
              />
              <label style={{fontWeight:'500',fontSize:'0.97rem',color:'#64748b',marginBottom:'2px',marginTop:'4px',display:'block',lineHeight:'1.1'}}>Autor</label>
              <input
                type="text"
                name="autor"
                value={form.autor}
                onChange={handleChange}
                disabled={!editMode || deleted}
                required
              />
              <label style={{fontWeight:'500',fontSize:'0.97rem',color:'#64748b',marginBottom:'2px',marginTop:'4px',display:'block',lineHeight:'1.1'}}>Gênero</label>
              <select
                name="genero"
                value={form.genero}
                onChange={handleChange}
                disabled={!editMode || deleted}
                required
              >
                {GENEROS.filter(g => g.value).map(g => (
                  <option key={g.value} value={g.value}>{g.label}</option>
                ))}
              </select>
              {error && <div className="sgb-error">{error}</div>}
              {success && <div className="sgb-success">{success}</div>}
              <div className="sgb-modal-actions">
                {livro.disponibilidade === 'DISPONIVEL' && !editMode && !deleted && (
                  <button type="button" className="sgb-btn-emprestar" onClick={handleEmprestar} disabled={deleted}>Emprestar</button>
                )}
                {isAdminOrBiblio && !editMode && !deleted && (
                  <button type="button" className="sgb-btn-editar" onClick={() => { setEditMode(true); clearSuccessMsg && clearSuccessMsg(); }} disabled={deleted}>Editar</button>
                )}
                {isAdminOrBiblio && editMode && !deleted && (
                  <button type="submit" disabled={loading || deleted}>Salvar</button>
                )}
                {isAdminOrBiblio && !deleted && (
                  <button type="button" className="sgb-btn-excluir" onClick={handleDelete} disabled={deleted || loading}>Excluir</button>
                )}
              </div>
            </form>
          </div>
        )}
        {showEmprestimoModal && (
          <EmprestimoModal
            livro={livro}
            onClose={() => { setShowEmprestimoModal(false) }}
            perfil={user?.perfil || localStorage.getItem('perfil')}
          />
        )}
      </>
    );
  }

  // --- Novo Modal de Empréstimo ---
  function EmprestimoModal({ livro, onClose, perfil }) {
    const [usuarios, setUsuarios] = useState([]);
    const [usuario, setUsuario] = useState('');
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState('');
    const [error, setError] = useState('');
    // Datas
    const hoje = new Date();
    const dataRetirada = hoje.toISOString().slice(0, 10);
    const dataPrevista = new Date(hoje.getTime() + 14 * 24 * 60 * 60 * 1000).toISOString().slice(0, 10);
    const isUsuario = perfil === 'USUARIO';

    useEffect(() => {
      if (!isUsuario) {
        fetch('http://localhost:8080/usuarios?perfil=USUARIO', {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
        })
          .then(res => res.json())
          .then(data => setUsuarios(Array.isArray(data) ? data : []))
          .catch(() => setUsuarios([]));
      }
    }, [isUsuario]);

    function handleSalvar(e) {
      e.preventDefault();
      setLoading(true);
      setError('');
      setSuccess('');
      let codigologin;
      if (isUsuario) {
        codigologin = localStorage.getItem('userId');
      } else {
        codigologin = usuario;
      }
      if (!codigologin) {
        setError('Selecione o usuário.');
        setLoading(false);
        return;
      }
      const body = {
        livro: { codigolivro: livro.codigolivro },
        usuario: { codigologin: Number(codigologin) },
        dataderetirada: dataRetirada,
        data_prevista: dataPrevista,
      };
      fetch('http://localhost:8080/emprestimos', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify(body),
      })
        .then(res => res.ok ? res.json() : Promise.reject(res))
        .then(() => {
          setSuccess('Empréstimo realizado com sucesso!');
        })
        .catch(async err => {
          let msg = 'Erro ao realizar empréstimo.';
          if (err && err.json) {
            try { const data = await err.json(); if (data && data.message) msg = data.message; } catch {}
          }
          setError(msg);
        })
        .finally(() => setLoading(false));
    }

    return (
      <div className="sgb-modal-bg">
        <form className="sgb-modal-form" onSubmit={handleSalvar}>
          <button className="sgb-modal-close-x" onClick={success ? () => {onClose(); setShowLivro(null);} : onClose } type="button" title="Fechar">×</button>
          <h3>Novo Empréstimo</h3>
          <label style={{fontWeight:'500',fontSize:'0.97rem',color:'#64748b',marginBottom:'2px',marginTop:'4px',display:'block',lineHeight:'1.1'}}>Livro</label>
          <input type="text" value={`${livro.nome} #${livro.codigolivro}`} disabled style={{marginBottom:'0.5rem'}}/>
          {!isUsuario && (
            <>
              <label style={{fontWeight:'500',fontSize:'0.97rem',color:'#64748b',marginBottom:'2px',marginTop:'4px',display:'block',lineHeight:'1.1'}}>Usuário</label>
              <select value={usuario} onChange={e => setUsuario(e.target.value)} required style={{marginBottom:'0.5rem'}} placeholder="Selecione o usuário">
                <option value="">Usuário</option>
                {usuarios.map(u => (
                  <option key={u.codigologin} value={u.codigologin}>{u.nome} #{u.codigologin}</option>
                ))}
              </select>
            </>
          )}
          <label style={{fontWeight:'500',fontSize:'0.97rem',color:'#64748b',marginBottom:'2px',marginTop:'4px',display:'block',lineHeight:'1.1'}}>Data de Retirada</label>
          <input type="date" value={dataRetirada} disabled style={{marginBottom:'0.5rem'}} />
          <label style={{fontWeight:'500',fontSize:'0.97rem',color:'#64748b',marginBottom:'2px',marginTop:'4px',display:'block',lineHeight:'1.1'}}>Data Prevista de Entrega</label>
          <input type="date" value={dataPrevista} disabled style={{marginBottom:'0.5rem'}} />
          {error && <div className="sgb-error">{error}</div>}
          {success && <div className="sgb-success">{success}</div>}
          <div className="sgb-modal-actions">
            {!success && (
              <button type="submit" disabled={loading}>{loading ? 'Salvando...' : 'Salvar'}</button>
            )}
          </div>
        </form>
      </div>
    );
  }

  return (
    <>
      <h2>Livros</h2>
      <div className="sgb-livros-filtros">
        {isAdminOrBiblio && (
          <button className="sgb-btn-criar-livro" onClick={() => setShowCreate(true)}>
            + Criar Livro
          </button>
        )}
        <input
          type="text"
          name="nome"
          placeholder="Filtrar por nome"
          value={filtros.nome}
          onChange={handleFiltroChange}
        />
        <input
          type="text"
          name="autor"
          placeholder="Filtrar por autor"
          value={filtros.autor}
          onChange={handleFiltroChange}
        />
        <select name="genero" value={filtros.genero} onChange={handleFiltroChange}>
          {GENEROS.map(g => <option key={g.value} value={g.value}>{g.label}</option>)}
        </select>
        <select name="disponibilidade" value={filtros.disponibilidade} onChange={handleFiltroChange}>
          {DISPONIBILIDADES.map(d => <option key={d.value} value={d.value}>{d.label}</option>)}
        </select>
      </div>
      {showCreate && (
        <CriarLivroForm onClose={() => setShowCreate(false)} onCreated={handleLivroCriado} />
      )}
      {livroCriadoMsg && (
        <div className="sgb-success" style={{textAlign:'center',marginTop:'2rem'}}>{livroCriadoMsg}</div>
      )}
      {showLivro && (
        <LivroModal
          livro={showLivro}
          onClose={() => { setShowLivro(null); setLivroCriadoMsg(''); }}
          onUpdated={buscarLivros}
          isAdminOrBiblio={isAdminOrBiblio}
        />
      )}
      {error && <p className="sgb-error">{error}</p>}
      <div className="sgb-livros-list">
        {livrosFiltrados.map(livro => (
          <LivroCard
            key={livro.codigolivro}
            livro={livro}
            onClick={/*isAdminOrBiblio ?*/ () => setShowLivro(livro) /*: undefined*/}
            isClickable={isAdminOrBiblio}
          />
        ))}
        {(!loading && livrosFiltrados.length === 0 && !error) && <p>Nenhum livro encontrado.</p>}
      </div>
    </>
  );
}

