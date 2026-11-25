# Academia UNIFOR (Unifor Gym)

Aplicativo Android desenvolvido em **Kotlin** e **XML** com integração ao **Firebase (Authentication e Cloud Firestore)** para o gerenciamento de rotinas de treino, acompanhamento físico e operação interna da Academia UNIFOR.

> **Status do Projeto:** Em desenvolvimento ativo (estruturação de fluxos, regras de negócio e persistência no Firestore).

---

## Perfis de Acesso e Funcionalidades

### 1. Aluno
- **Cadastro e Triagem Obrigatória:** Fluxo bloqueante em 5 etapas (objetivo e rotina, biometria/IMC, questionário PAR-Q, histórico de lesões e termo de responsabilidade) exigido antes de liberar o acesso à Home.
- **Home e Acompanhamento:** Visualização da lotação atual da academia, treino do dia, próximos treinos e acesso rápido ao minimapa.
- **Mapa da Academia:** Planta baixa setorizada por grupos musculares e equipamentos (Pernas, Costas, Braços, Peito, Pesos e Totens).
- **Perfil e Agendamentos:** Consulta aos dados biométricos e anamnese preenchidos na triagem, inscrição em aulas coletivas e área financeira (cópia de código PIX e simulação de boleto).

### 2. Instrutor
- **Painel de Controle:** Monitoramento em tempo real da lotação da academia, contador de alunos aguardando ficha e últimas inscrições em aulas coletivas.
- **Biblioteca de Treinos:** Criação de modelos reutilizáveis de treino (Treinos A, B e C) com definição opcional de prazo de validade padrão.
- **Gerenciamento de Alunos:** Busca por nome ou CPF, filtros por status (`Ficha Ativa`, `Aguardando Ficha`, `Ficha Vencida`), consulta de restrições médicas/PAR-Q da triagem e associação de treinos personalizados ou da biblioteca com cálculo automático de vencimento.
- **Perfil e Aulas Coletivas:** Gestão de turno, acompanhamento de fichas criadas e visualização da ocupação de vagas nas turmas.

### 3. Administrador (Adm)
- **Acesso Completo de Instrutor:** Compartilha todas as ferramentas operacionais de montagem de treinos, acompanhamento de alunos e aulas coletivas.
- **Gestão de Funcionários:** Acesso à lista de funcionários cadastrados e fluxo de aprovação ou rejeição de novos cadastros da equipe.

---

## Tecnologias Utilizadas

- **Linguagem:** Kotlin
- **Interface (UI):** XML Layouts (`ConstraintLayout`, `RecyclerView`, `BottomNavigationView`, `CardView`)
- **Backend / Banco de Dados:** Firebase Authentication & Cloud Firestore
- **IDE:** Android Studio

---

## Estrutura Básica no Firestore

- `usuarios`: Armazena os perfis cadastrados (alunos, instrutores e administradores), dados da triagem inicial e o status atual da ficha (`statusFicha`).
- `usuarios/{alunoId}/fichas`: Subcoleção que registra a ficha ativa vinculada ao aluno, incluindo `dataInicio`, `prazoValidadeDias` e `dataVencimento`.

---

## Como Executar Localmente

1. Clone este repositório e abra o projeto no **Android Studio**.
2. Certifique-se de que o arquivo `google-services.json` vinculado ao projeto Firebase esteja configurado no diretório `app/`.
3. Sincronize o Gradle (`Sync Project with Gradle Files`) e execute o aplicativo em um emulador ou dispositivo Android.
