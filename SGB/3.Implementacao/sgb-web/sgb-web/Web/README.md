# SGB Web

Projeto React criado com Vite para consumir uma API rodando na porta 8080.

## Funcionalidades
- Página de login com formulário de email e senha
- Chama `/login` na API (porta 8080) e armazena o token JWT retornado
- Após login, exibe dados do usuário e permite logout

## Como rodar

1. Instale as dependências:
   ```powershell
   npm install
   ```
2. Inicie o projeto:
   ```powershell
   npm run dev
   ```
3. Acesse [http://localhost:5173](http://localhost:5173) no navegador.

> Certifique-se de que a API está rodando em http://localhost:8080
