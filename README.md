#🚀 [SGB-Sistema de Gerenciamento de Biblioteca]

O projeto SGB-Sistema de Gerenciamento de Biblioteca possui frontend desenvolvido em React e Backend Java/Spring Boot, tendo o banco de dados em PostgreSQL e estando implantado na AWS

##📁 Estrutura do projeto
/1.Requisitos
    ├──Casos de Uso
        ├──SGB - Historia de Usuario 01- Realizar Login.docx
        ├──SGB - Historia de Usuario 02- Manter Livros.docx
        ├──SGB - Historia de Usuario 03 - Manter Emprestimos .docx
        ├──SGB - Historia de Usuario 04- Manter Gênero.docx
        ├──SGB - Historia de Usuario 05-Manter Usuario.docx
        ├──SGB - Historia de Usuario 06- Realizar Cadastro.docx
    ├──SGB-Visão.docx
/2.Analise e Design
    ├──SGB - Modelo de Analise e Design.asta
/3.Implementacao
    ├── SGB---Backend (submodulo)
    ├── SGB---Frontend (submodulo)
    ├── Dockerfile
/4.Teste
    ├──SGB-Roteiro de Testes.xlsx
/5.Implantação
    ├──SGB - Guia de Implantação.docx
    ├──SGB - Manual do Usuário.docx
    ├──SGB - Script.docx
/6.Gerenciamento de Projeto
    ├──SGB - Planejamento e Controle do Projeto.xlsx
    ├──SGB - Checklist Verificacao de Projeto.xlsx
.gitmodules
/README.md

##Pasta Descrição
1.Requisitos -> Contém documentos de levantamento de requisitos e história de usuario
2.Analise e Design -> Diagramas UML, arquitetura, modelagem ER e fluxogramas
3.Implementacao	-> Código-fonte dividido em backend e frontend e Dockerfile
4.Teste -> Roteiro de Testes realizados no sistema
5.Implantação -> Documentação de Implantação, Manual de Usuario e scripts de deploy
6.Gerenciamento de Projeto -> Cronograma, riscos e planejamento

##🏗 Arquitetura da aplicação

##![Diagrama de Arquitetura](2.Analise%20e%20Design/SGB-Diagrama-AWS.png)


##⚙ Como executar o projeto (Playbook) -> Guia de Implantação
##✅ Pré-requisitos

Git instalado

Docker instalado

Docker Compose instalado

Verifique:

docker --version
docker compose version
▶ Passo a passo

Clone o repositório

git clone https://github.com/GustavoFrossard/SGB.git
git submodule update --init --recursive

Entre na pasta de implementação

cd SGB/3.Implementacao

Suba os containers

docker build -t sgb-app .
docker run -p 8080:80 sgb-app

Acesse no navegador:


http://localhost:8080


##🔐 Variáveis de ambiente

As variáveis estão definidas em um arquivo .env.
Por segurança as variaveis de banco de dados não estão contidas no repositorio


##🧩 Componentes do sistema
Componente
Backend [Java/SpringBoot]
Frontend [React]
Banco de dados [PostgreSQL]
Autenticação [JWT]

##🧩 Componentes da Nuvem
EC2
RDS/PostgreSQL
Internet Gateway e NAT gateway
Load Balancer
Target Groups
VPC
Launch Template
AutoScaling
Tabela de Rotas Publicas/Privadas

## 👨‍💻 Autores

**Gabriel Cândido**
**Gustavo Frossard**
**Juliano Vasques**
**Nicolas Campos**
**Thiago Maschietto**
Estudantes de Ciência da Computação

---
