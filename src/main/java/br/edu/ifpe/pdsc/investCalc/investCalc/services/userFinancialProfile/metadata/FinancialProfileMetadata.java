package br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile.metadata;

import java.util.List;

import br.edu.ifpe.pdsc.investCalc.investCalc.enums.userFinancialProfile.FinancialProfile;

public final class FinancialProfileMetadata {

    private FinancialProfileMetadata() {
    }

    public static FinancialProfileDetails get(FinancialProfile profile) {

        return switch (profile) {

            case DEVEDOR -> new FinancialProfileDetails(

                    "Você está em uma fase de recuperação financeira. O foco principal deve ser reduzir dívidas e recuperar sua estabilidade financeira.",

                    List.of(
                            "Reconhece a necessidade de melhorar."),

                    List.of(
                            "Alto comprometimento da renda.",
                            "Baixa capacidade de poupança."),

                    List.of(
                            "Criar orçamento mensal.",
                            "Priorizar quitação de dívidas.",
                            "Evitar novas dívidas."),

                    List.of(
                            "Quitar cartão de crédito.",
                            "Criar reserva de R$ 1.000."));

            case GASTADOR -> new FinancialProfileDetails(

                    "Você possui renda, mas seus hábitos de consumo dificultam a construção de patrimônio.",

                    List.of(
                            "Possui potencial de geração de renda."),

                    List.of(
                            "Consumo impulsivo."),

                    List.of(
                            "Definir limite mensal.",
                            "Revisar gastos supérfluos."),

                    List.of(
                            "Reduzir despesas em 10%.",
                            "Poupar o primeiro salário completo."));

            case DESLIGADO -> new FinancialProfileDetails(

                    "Seu principal desafio é adquirir consciência financeira e criar hábitos básicos de controle.",

                    List.of(
                            "Possui espaço para rápida evolução."),

                    List.of(
                            "Falta de acompanhamento financeiro."),

                    List.of(
                            "Registrar todas as receitas e despesas.",
                            "Consultar o dashboard semanalmente."),

                    List.of(
                            "Registrar gastos por 30 dias consecutivos."));

            case POUPADOR -> new FinancialProfileDetails(

                    "Você possui boa organização financeira e já constrói segurança financeira para o futuro.",

                    List.of(
                            "Organização financeira."),

                    List.of(
                            "Dinheiro pode estar parado."),

                    List.of(
                            "Aprender sobre investimentos.",
                            "Diversificar aplicações."),

                    List.of(
                            "Criar reserva de emergência completa.",
                            "Iniciar carteira de investimentos."));

            case INVESTIDOR -> new FinancialProfileDetails(

                    "Você já possui uma base financeira sólida e utiliza investimentos para acelerar seus objetivos.",

                    List.of(
                            "Visão de longo prazo.",
                            "Disciplina financeira."),

                    List.of(
                            "Possível excesso de confiança."),

                    List.of(
                            "Diversificar patrimônio.",
                            "Revisar metas periodicamente."),

                    List.of(
                            "Independência financeira.",
                            "Aumento da renda passiva."));
        };
    }
}
