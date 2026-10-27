<div align="center">

# 🛍️ LojaFlex — App Mobile

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Firebase](https://img.shields.io/badge/Firebase-Services-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)

<p align="center">
  <b>Seu negócio, mais perto de todos.</b><br>
  Uma solução mobile completa para pequenos comerciantes, microempreendedores e lojistas de bairro.
</p>

---

</div>

## 📌 Sobre o Projeto

O **LojaFlex** é um aplicativo mobile desenvolvido no âmbito do Centro Universitário **UNICEPLAC** para a disciplina de *Projeto Integrado de Desenvolvimento Mobile (ENSOF5AM2)*.

O objetivo principal é digitalizar e simplificar a gestão do comércio local, eliminando controles manuais (cadernos e planilhas) e fornecendo uma plataforma moderna para visualização de produtos, controle de pedidos e gestão de lojas.

---

## 🎯 Proposta de Valor (Business Model Canvas)

* ⚡ **Gestão Simplificada:** Facilidade no controle diário de vendas e catálogo.
* 📦 **Organização de Produtos:** Centralização dos itens com imagens, categorias e valores.
* 📱 **Acesso em Qualquer Lugar:** Aplicativo leve, intuitivo e responsivo para smartphones.
* 📊 **Tomada de Decisão:** Informações centralizadas para auxílio no crescimento do negócio.

---

## 🛠️ Arquitetura e Tecnologias

A aplicação utiliza uma arquitetura em camadas bem definides, separando a interface do usuário, as regras de negócio e a persistência de dados.

```text
┌─────────────────────────┐       Recomposições JSON       ┌─────────────────────────┐
│   App Mobile (Android)  │ ◄───────────────────────────► │      Back-End (API)     │
│   • Kotlin              │   Requisições HTTP/HTTPS      │   • Java + Spring Boot  │
│   • Jetpack Compose     │                               │   • RESTful APIs        │
│   • Material Design 3   │                               │   • Regras de Negócio   │
└────────────┬────────────┘                               └────────────┬────────────┘
             │                                                         │
             │ Autenticação / Mídia                                    │ Persistência SQL
             ▼                                                         ▼
┌─────────────────────────┐                               ┌─────────────────────────┐
│    Serviços Externos    │                               │     Banco de Dados      │
│   • Firebase Auth       │                               │   • MySQL Server        │
│   • Firebase Storage    │                               │   • Modelagem Relacional│
└─────────────────────────┘                               └─────────────────────────┘
