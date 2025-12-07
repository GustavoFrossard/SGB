# SGB — Sistema de Gerenciamento de Biblioteca

## Sistema de Gerenciamento de Biblioteca

Este repositório contém o **Sistema de Gerenciamento de Biblioteca (SGB)**, desenvolvido com front-end em **React**, back-end em **Java/Spring Boot** e banco de dados **PostgreSQL**, totalmente implantado na **AWS**.

O projeto contempla todas as etapas do ciclo de desenvolvimento de software: **levantamento de requisitos, análise e design, implementação, testes, implantação e gerenciamento de projeto**.

---

## 📑 Sumário

- Estrutura do Repositório  
- Conteúdo do Projeto  
- Arquitetura do Sistema  
- Guia de Implantação  
- Instruções de Instalação Rápida  
- Componentes do Sistema  
- Componentes da Nuvem  
- Autores  

---

## 📁 Estrutura do Repositório

```
1.Requisitos
│ ├── Casos de Uso
│ │ ├── SGB - Historia de Usuario 01 - Realizar Login.docx
│ │ ├── SGB - Historia de Usuario 02 - Manter Livros.docx
│ │ ├── SGB - Historia de Usuario 03 - Manter Emprestimos.docx
│ │ ├── SGB - Historia de Usuario 04 - Manter Gênero.docx
│ │ ├── SGB - Historia de Usuario 05 - Manter Usuario.docx
│ │ ├── SGB - Historia de Usuario 06 - Realizar Cadastro.docx
│ └── SGB - Visão.docx
│
2.Analise e Design
│ └── SGB - Modelo de Analise e Design.asta
│
3.Implementacao
│ ├── SGB---Backend (submódulo)
│ ├── SGB---Frontend (submódulo)
│ └── Dockerfile
│
4.Teste
│ └── SGB - Roteiro de Testes.xlsx
│
5.Implantação
│ ├── SGB - Guia de Implantação.docx
│ ├── SGB - Manual do Usuário.docx
│ └── SGB - Script.docx
│
6.Gerenciamento de Projeto
│ ├── SGB - Planejamento e Controle do Projeto.xlsx
│ └── SGB - Checklist Verificacao de Projeto.xlsx
│
.gitmodules
README.md
```
## 📌 Conteúdo do Projeto

### 1. Requisitos
Documentos contendo:
- Casos de uso  
- Histórias de usuário  
- Documento de visão do sistema  

### 2. Análise e Design
- Diagramas UML  
- Arquitetura do sistema  
- Modelagem de dados (ER)  

### 3. Implementação
- **Back-end:** Java + Spring Boot  
- **Front-end:** React  
- **Dockerfile:** Configuração para facilitar deploy  

### 4. Teste
- Roteiro de testes realizado no sistema  

### 5. Implantação
- Guia completo de implantação  
- Script para deploy  
- Manual do usuário  

### 6. Gerenciamento de Projeto
- Cronograma e planejamento  
- Checklist de verificação  

## 🏗 Arquitetura do Sistema

![Diagrama de Arquitetura](2.Analise%20e%20Design/SGB-Diagrama-AWS.png)

**Figura — Arquitetura do sistema hospedado na AWS.**


##⚙ Como executar o projeto (Playbook) -> Guia de Implantação
##✅ Pré-requisitos

- Git instalado

- Docker instalado

- Docker Compose instalado

- Verifique:

```bash
- docker --version
- docker compose version
- ▶ Passo a passo
```

- Clone o repositório
```bash
- git clone https://github.com/GustavoFrossard/SGB.git
- git submodule update --init --recursive
```
Entre na pasta de implementação
```bash
cd SGB/3.Implementacao
```
Suba os containers
```bash
docker build -t sgb-app .
docker run -p 8080:80 sgb-app
```
##Acesse no navegador:

http://localhost:8080


##🔐 Variáveis de ambiente

-As variáveis estão definidas em um arquivo .env.
-Por segurança as variaveis de banco de dados não estão contidas no repositorio


##🧩 Componentes do sistema
-Backend [Java/SpringBoot]
-Frontend [React]
-Banco de dados [PostgreSQL]
-Autenticação [JWT]

##🧩 Componentes da Nuvem
-EC2
-RDS/PostgreSQL
-Internet Gateway e NAT gateway
-Load Balancer
-Target Groups
-VPC
-Launch Template
-AutoScaling
-Tabela de Rotas Publicas/Privadas

## 👨‍💻 Autores

- **Gabriel Cândido**
- **Gustavo Frossard**
- **Juliano Vasques**
- **Nicolas Campos**
- **Thiago Maschietto**
- Estudantes de Ciência da Computação

---
