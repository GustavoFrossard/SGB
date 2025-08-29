import { useEffect, useState } from 'react';

export default function EmprestimosPage({ user }) {
  const [emprestimos, setEmprestimos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  // emAtraso: '', 'true', 'false', 'entregue'
  const [filtros, setFiltros] = useState({ emAtraso: '', usuario: '', codLivro: '' });
  const [usuarios, setUsuarios] = useState([]);
  const [livros, setLivros] = useState([]);
  const [showModalEmprestimo, setShowModalEmprestimo] = useState(null);

  // Determina perfil do usuário
  const perfil = user?.perfil || localStorage.getItem('perfil');
  const isUsuario = perfil === 'USUARIO';

  // Buscar usuários para o select
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

  // Buscar livros para o select
  useEffect(() => {
    fetch('http://localhost:8080/emprestimos', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
    })
      .then(res => res.json())
      .then(data => {
        if (Array.isArray(data)) {
          // Extrai pares únicos codLivro/nomeLivro
          const livrosUnicos = [];
          const seen = new Set();
          data.forEach(e => {
            const key = `${e.codLivro}|${e.nomeLivro}`;
            if (!seen.has(key)) {
              seen.add(key);
              livrosUnicos.push({ codLivro: e.codLivro, nomeLivro: e.nomeLivro });
            }
          });
          setLivros(livrosUnicos);
        } else {
          setLivros([]);
        }
      })
      .catch(() => setLivros([]));
  }, []);

  // Atualiza lista ao alterar filtros
  useEffect(() => {
    setLoading(true);
    setError('');
    const params = [];
    if (filtros.emAtraso === 'true') params.push('em_atraso=true');
    if (filtros.emAtraso === 'false') params.push('em_atraso=false');
    if (filtros.emAtraso === 'entregue') params.push('entregue=true');
    if (filtros.usuario && !isUsuario) params.push(`usuario=${filtros.usuario}`);
    if (filtros.codLivro) params.push(`cod_livro=${filtros.codLivro}`);
    const query = params.length ? `?${params.join('&')}` : '';
    fetch(`http://localhost:8080/emprestimos${query}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`,
      },
    })
      .then(res => res.json())
      .then(data => {
        setEmprestimos(Array.isArray(data) ? data : []);
      })
      .catch(() => setError('Erro ao buscar empréstimos.'))
      .finally(() => setLoading(false));
  }, [filtros, isUsuario]);

  function handleFiltroChange(e) {
    const { name, value } = e.target;
    setFiltros(f => ({ ...f, [name]: value }));
  }

  function handleCardClick(emprestimo) {
    if (!isUsuario) setShowModalEmprestimo(emprestimo);
  }

  function handleCloseModalEmprestimo(atualizou) {
    setShowModalEmprestimo(null);
    if (atualizou) {
      // Recarrega os cards
      const params = [];
      if (filtros.emAtraso === 'true') params.push('em_atraso=true');
      if (filtros.emAtraso === 'false') params.push('em_atraso=false');
      if (filtros.emAtraso === 'entregue') params.push('entregue=true');
      if (filtros.usuario && !isUsuario) params.push(`usuario=${filtros.usuario}`);
      if (filtros.codLivro) params.push(`cod_livro=${filtros.codLivro}`);
      const query = params.length ? `?${params.join('&')}` : '';
      fetch(`http://localhost:8080/emprestimos${query}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
      })
        .then(res => res.json())
        .then(data => {
          setEmprestimos(Array.isArray(data) ? data : []);
        })
        .catch(() => setError('Erro ao buscar empréstimos.'));
    }
  }

  return (
    <>
      <h2>Empréstimos</h2>
      <div className="sgb-livros-filtros" style={{ marginBottom: '1.5rem' }}>
        <select
          name="emAtraso"
          value={filtros.emAtraso}
          onChange={handleFiltroChange}
          style={{ minWidth: 140 }}
        >
          <option value="">Todos</option>
          <option value="true">Em atraso</option>
          <option value="false">Em dia</option>
          <option value="entregue">Entregue</option>
        </select>
        {!isUsuario && (
          <select
            name="usuario"
            value={filtros.usuario}
            onChange={handleFiltroChange}
            style={{ minWidth: 180 }}
          >
            <option value="">Selecione o usuário</option>
            {usuarios.map(u => (
              <option key={u.codigologin} value={u.codigologin}>{u.nome} #{u.codigologin}</option>
            ))}
          </select>
        )}
        <select
          name="codLivro"
          value={filtros.codLivro}
          onChange={handleFiltroChange}
          style={{ minWidth: 180 }}
        >
          <option value="">Selecione o livro</option>
          {livros.map(l => (
            <option key={l.codLivro} value={l.codLivro}>{l.nomeLivro} #{l.codLivro}</option>
          ))}
        </select>
      </div>
      {error && <p className="sgb-error">{error}</p>}
      <div className="sgb-emprestimos-list">
        {emprestimos.map(emprestimo => (
          <EmprestimoCard key={emprestimo.codigoemprestimo} emprestimo={emprestimo} onClick={() => handleCardClick(emprestimo)} isClickable={!isUsuario} />
        ))}
        {(!loading && emprestimos.length === 0 && !error) && <p>Nenhum empréstimo encontrado.</p>}
      </div>
      {showModalEmprestimo && (
        <EmprestimoModal
          emprestimo={showModalEmprestimo}
          onClose={handleCloseModalEmprestimo}
        />
      )}
    </>
  );
}

function EmprestimoModal({ emprestimo, onClose }) {
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');
  const [editEntrega, setEditEntrega] = useState(false);
  const [dataEntrega, setDataEntrega] = useState(emprestimo.dataDeEntrega ? emprestimo.dataDeEntrega.slice(0, 10) : new Date().toISOString().slice(0, 10));
  const [emprestimoAtual, setEmprestimoAtual] = useState(emprestimo);
  const [atualizou, setAtualizou] = useState(false);
  const entregue = Boolean(emprestimoAtual.dataDeEntrega);

  function handleReceber() {
    setEditEntrega(true);
  }

  function handleSalvar(e) {
    e.preventDefault();
    setLoading(true);
    setError('');
    fetch(`http://localhost:8080/emprestimos/${emprestimoAtual.codigoemprestimo}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${localStorage.getItem('token')}`,
      },
      body: JSON.stringify({ datadeentrega: dataEntrega }),
    })
      .then(res => {
        console.log('Resposta do PUT /emprestimos:', res);
        return res.ok ? res.json() : Promise.reject(res);
      })
      .then(data => {
        setSuccess('Empréstimo recebido com sucesso!');
        setEmprestimoAtual({ ...emprestimoAtual, ...data, dataDeEntrega: dataEntrega });
        setEditEntrega(false);
        setAtualizou(true);

      })
      .catch(async err => {
        
        let msg = 'Erro ao salvar entrega. '+await err.text();

        setError(msg);
      })
      .finally(() => setLoading(false));
  }

  function handleExcluir() {
    if (!window.confirm('Tem certeza que deseja excluir este empréstimo?')) return;
    setLoading(true);
    setError('');
    fetch(`http://localhost:8080/emprestimos/${emprestimoAtual.codigoemprestimo}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
    })
      .then(res => {
        if (res.ok) {
          setSuccess('Empréstimo excluído com sucesso!');
          setAtualizou(true);
          setTimeout(() => onClose(true), 1200);
        } else {
          setError('Erro ao excluir empréstimo.');
        }
      })
      .catch(() => setError('Erro ao excluir empréstimo.'))
      .finally(() => setLoading(false));
  }

  return (
    <div className="sgb-modal-bg">
      <form className="sgb-modal-form" onSubmit={handleSalvar}>
        <button className="sgb-modal-close-x" onClick={() => onClose(atualizou)} type="button" title="Fechar">×</button>
        <h3>Empréstimo #{emprestimoAtual.codigoemprestimo}</h3>
        <div style={{marginBottom:'0.5rem'}}>
          <b>Livro:</b> {emprestimoAtual.nomeLivro} #{emprestimoAtual.codLivro}<br/>
          <b>Usuário:</b> {emprestimoAtual.nomeUsuario} #{emprestimoAtual.codUsuario}<br/>
          <b>Retirada:</b> {formatDataBR(emprestimoAtual.dataDeRetirada)}<br/>
          <b>Prevista:</b> {formatDataBR(emprestimoAtual.dataPrevista)}<br/>
          <b>Entrega:</b> {editEntrega ? (
            <input type="date" value={dataEntrega} onChange={e => setDataEntrega(e.target.value)} required style={{marginLeft:4}} />
          ) : (
            formatDataBR(emprestimoAtual.dataDeEntrega)
          )}<br/>
          <div className="sgb-emprestimo-status-row">
            {!Boolean(emprestimoAtual.dataDeEntrega) && emprestimoAtual.emAtraso && (
              <span className="sgb-emprestimo-status-atraso">Em atraso ({emprestimoAtual.diasEmAtraso} dias)</span>
            )}
            {!Boolean(emprestimoAtual.dataDeEntrega) && emprestimoAtual.saldoDevedor > 0 && (
              <div className="sgb-emprestimo-saldo">Saldo devedor: R$ {emprestimoAtual.saldoDevedor.toFixed(2)}</div>
            )}
            {!emprestimoAtual.emAtraso && !Boolean(emprestimoAtual.dataDeEntrega) && <span className="sgb-emprestimo-tag-emdia">Em dia</span>}
            {Boolean(emprestimoAtual.dataDeEntrega) && (
              <div className="sgb-emprestimo-tag-entregue">Entregue</div>
            )}
          </div>
        </div>
        {error && <div className="sgb-error">{error}</div>}
        {success && <div className="sgb-success">{success}</div>}
        <div className="sgb-modal-actions">
          {!entregue && !editEntrega && (
            <button className="sgb-btn-emprestar" type="button" onClick={handleReceber} disabled={loading}>Receber</button>
          )}
          {!entregue && editEntrega && (
            <button className="sgb-btn-salvar" type="submit" disabled={loading}>{loading ? 'Salvando...' : 'Salvar'}</button>
          )}
          <button className="sgb-btn-excluir" type="button" onClick={handleExcluir} hidden={loading}>Excluir</button>
        </div>
      </form>
    </div>
  );
}

function formatDataBR(data) {
  if (!data) return '-';
  const d = new Date(data);
  if (isNaN(d)) return data;
  return d.toLocaleDateString('pt-BR');
}

function EmprestimoCard({ emprestimo, onClick, isClickable }) {
  const entregue = Boolean(emprestimo.dataDeEntrega);
  const emDia = !emprestimo.emAtraso && !entregue;
  return (
    <div
      className={`sgb-emprestimo-card${emprestimo.emAtraso ? ' sgb-emprestimo-atrasado' : ''}${isClickable ? ' sgb-emprestimo-card-clickable' : ''}`}
      style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between', cursor: isClickable ? 'pointer' : undefined }}
      onClick={isClickable ? onClick : undefined}
      tabIndex={isClickable ? 0 : undefined}
    >
      <div>
        <div className="sgb-emprestimo-codigo" style={{ marginBottom: '0.5rem' }}>Empréstimo #{emprestimo.codigoemprestimo}</div>
        <div className="sgb-emprestimo-livro">
          <span className="sgb-emprestimo-livro-nome">
            Livro: <b>{emprestimo.nomeLivro} #{emprestimo.codLivro}</b>
          </span>
        </div>
        <div className="sgb-emprestimo-usuario">
          Usuário: {emprestimo.nomeUsuario} #{emprestimo.codUsuario}
        </div>
        <div className="sgb-emprestimo-datas">
          <span>Retirada: {formatDataBR(emprestimo.dataDeRetirada)}</span>
          <span>Prevista: {formatDataBR(emprestimo.dataPrevista)}</span>
          <span>Entrega: {formatDataBR(emprestimo.dataDeEntrega)}</span>
        </div>
      </div>
      <div className="sgb-emprestimo-status-row">
        {emprestimo.emAtraso && (
          <span className="sgb-emprestimo-status-atraso">Em atraso ({emprestimo.diasEmAtraso} dias)</span>
        )}
        {emprestimo.saldoDevedor > 0 && (
          <div className="sgb-emprestimo-saldo">Saldo devedor: R$ {emprestimo.saldoDevedor.toFixed(2)}</div>
        )}
        {emDia && <span className="sgb-emprestimo-tag-emdia">Em dia</span>}
        {entregue && (
          <div className="sgb-emprestimo-tag-entregue">Entregue</div>
        )}
      </div>
    </div>
  );
}
