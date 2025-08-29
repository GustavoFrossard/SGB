import { FiBook, FiUsers, FiList } from 'react-icons/fi';
import reactLogo from './assets/icons8-user-50.png';

export default function SideMenu({ perfil, selected, onSelect }) {
  return (
    <aside className="sgb-menu">
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: '2.2rem' }}>
        <div style={{ fontWeight: 800, fontSize: '2.1rem', color: '#6366f1', letterSpacing: '1px', marginBottom: '0.2rem', marginTop: '-0.5rem' }}>SGB</div>
        <div style={{ fontSize: '0.68rem', color: '#64748b', fontWeight: 500, textAlign: 'center', lineHeight: 1.1 }}>Sistema de Gerenciamento de Biblioteca</div>
        <hr style={{ width: '70%', border: 0, borderTop: '4px solid #6366f1', borderRadius: '2px', margin: '1rem 0 0 0', boxShadow: '0 1px 6px #6366f133' }} />
      </div>
      <div className="sgb-menu-options">
        <button
          className={selected === 'livros' ? 'sgb-menu-btn selected' : 'sgb-menu-btn'}
          onClick={() => onSelect('livros')}
        >
          <FiBook size={22} /> Livros
        </button>
        <button
          className={selected === 'emprestimos' ? 'sgb-menu-btn selected' : 'sgb-menu-btn'}
          onClick={() => onSelect('emprestimos')}
        >
          <FiList size={22} /> Empréstimos
        </button>
        {(perfil === 'ADMIN' || perfil === 'BIBLIOTECARIO') && (
          <button
            className={selected === 'usuarios' ? 'sgb-menu-btn selected' : 'sgb-menu-btn'}
            onClick={() => onSelect('usuarios')}
          >
            <FiUsers size={22} /> Usuários
          </button>
        )}
      </div>
      <div className="sgb-menu-bottom">
        <button className="sgb-menu-profile" onClick={() => onSelect('perfil')} title="Meu Perfil">
          <img src={reactLogo} alt="Perfil" className="sgb-menu-profile-img" width="32" height="32" />
        </button>
      </div>
    </aside>
  );
}
