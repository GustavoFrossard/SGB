import { useEffect, useState } from 'react';
import LivrosPage from './LivrosPage';
import EmprestimosPage from './EmprestimosPage';
import UsuarioPage from './UsuarioPage';

export default function MainContent({ selected, user, onLogout }) {
  const isAdminOrBiblio = user?.perfil === 'ADMIN' || user?.perfil === 'BIBLIOTECARIO';

  return (
    <main className="sgb-main">
      {selected === 'livros' && (
        <LivrosPage user={user} isAdminOrBiblio={isAdminOrBiblio} onLogout={onLogout} />
      )}
      {selected === 'emprestimos' && (
        <EmprestimosPage />
      )}
      {selected === 'usuarios' && <UsuarioPage user={user} />}
      {selected === 'perfil' && (
        <div>
          <h2>Meu Perfil</h2>
          <div className="sgb-user">Usuário: {user.user}</div>
          <div className="sgb-perfil">Perfil: {user.perfil}</div>
          <button className="sgb-logout" onClick={onLogout}>Sair</button>
        </div>
      )}
    </main>
  );
}
