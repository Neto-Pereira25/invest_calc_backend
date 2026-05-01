package br.edu.ifpe.pdsc.investCalc.investCalc.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.edu.ifpe.pdsc.investCalc.investCalc.entities.Category;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.Subcategory;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.TransactionType;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.CategoryRepository;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.SubcategoryRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

        private final CategoryRepository categoryRepository;
        private final SubcategoryRepository subcategoryRepository;

        @Override
        public void run(String... args) {

                if (categoryRepository.count() > 0)
                        return;

                // DESPESAS
                Category habitation = categoryRepository.save(
                                new Category(null, "Despesa-Moradia", TransactionType.EXPENSE, null));

                subcategoryRepository.save(new Subcategory(null, "Aluguel", habitation));
                subcategoryRepository.save(new Subcategory(null, "Condominio", habitation));
                subcategoryRepository.save(new Subcategory(null, "IPTU", habitation));
                subcategoryRepository.save(new Subcategory(null, "Energia", habitation));
                subcategoryRepository.save(new Subcategory(null, "Agua", habitation));
                subcategoryRepository.save(new Subcategory(null, "Gas", habitation));
                subcategoryRepository.save(new Subcategory(null, "Manutencao-Residencial", habitation));
                subcategoryRepository.save(new Subcategory(null, "Reforma", habitation));
                subcategoryRepository.save(new Subcategory(null, "Moveis-Decoracao", habitation));
                subcategoryRepository.save(new Subcategory(null, "Outros", habitation));

                Category essentialService = categoryRepository.save(
                                new Category(null, "Despesa-Servico-Essencial", TransactionType.EXPENSE, null));

                subcategoryRepository.save(new Subcategory(null, "Internet", essentialService));
                subcategoryRepository.save(new Subcategory(null, "Telefone-Movel", essentialService));
                subcategoryRepository.save(new Subcategory(null, "Planos-Seguros", essentialService));
                subcategoryRepository.save(new Subcategory(null, "Outros", essentialService));

                Category food = categoryRepository.save(
                                new Category(null, "Despesa-Alimentacao", TransactionType.EXPENSE, null));

                subcategoryRepository.save(new Subcategory(null, "Supermercado", food));
                subcategoryRepository.save(new Subcategory(null, "Restaurante", food));
                subcategoryRepository.save(new Subcategory(null, "Delivery", food));
                subcategoryRepository.save(new Subcategory(null, "Cafeteria-Lanche", food));
                subcategoryRepository.save(new Subcategory(null, "Outros", food));

                Category transport = categoryRepository.save(
                                new Category(null, "Despesa-Transporte", TransactionType.EXPENSE, null));

                subcategoryRepository.save(new Subcategory(null, "Combustivel", transport));
                subcategoryRepository.save(new Subcategory(null, "Transporte-Publico", transport));
                subcategoryRepository.save(new Subcategory(null, "Uber", transport));
                subcategoryRepository.save(new Subcategory(null, "Manutencao-Veicular", transport));
                subcategoryRepository.save(new Subcategory(null, "Seguro-Veicular", transport));
                subcategoryRepository.save(new Subcategory(null, "Estacionamento", transport));
                subcategoryRepository.save(new Subcategory(null, "Pedagio", transport));
                subcategoryRepository.save(new Subcategory(null, "Multas", transport));
                subcategoryRepository.save(new Subcategory(null, "Outros", transport));

                Category health = categoryRepository.save(
                                new Category(null, "Despesa-Saude", TransactionType.EXPENSE, null));

                subcategoryRepository.save(new Subcategory(null, "Plano-Saude", health));
                subcategoryRepository.save(new Subcategory(null, "Farmacia", health));
                subcategoryRepository.save(new Subcategory(null, "Consultas", health));
                subcategoryRepository.save(new Subcategory(null, "Exames", health));
                subcategoryRepository.save(new Subcategory(null, "Terapia", health));
                subcategoryRepository.save(new Subcategory(null, "Psicologia", health));
                subcategoryRepository.save(new Subcategory(null, "Odontologia", health));
                subcategoryRepository.save(new Subcategory(null, "Outros", health));

                Category education = categoryRepository.save(
                                new Category(null, "Despesa-Educacao", TransactionType.EXPENSE, null));

                subcategoryRepository.save(new Subcategory(null, "Mensalidade-Escola", education));
                subcategoryRepository.save(new Subcategory(null, "Mensalidade-Universidade", education));
                subcategoryRepository.save(new Subcategory(null, "Cursos", education));
                subcategoryRepository.save(new Subcategory(null, "Livros", education));
                subcategoryRepository.save(new Subcategory(null, "Material-Escolar", education));

                Category leisureAndLifestyle = categoryRepository.save(
                                new Category(null, "Despesa-Lazer-Estilo-de-Vida", TransactionType.EXPENSE, null));

                subcategoryRepository.save(new Subcategory(null, "Streaming", leisureAndLifestyle));
                subcategoryRepository.save(new Subcategory(null, "Cinema", leisureAndLifestyle));
                subcategoryRepository.save(new Subcategory(null, "Evento", leisureAndLifestyle));
                subcategoryRepository.save(new Subcategory(null, "Hobbies", leisureAndLifestyle));
                subcategoryRepository.save(new Subcategory(null, "Viagem", leisureAndLifestyle));
                subcategoryRepository.save(new Subcategory(null, "Hotel", leisureAndLifestyle));
                subcategoryRepository.save(new Subcategory(null, "Passeio", leisureAndLifestyle));
                subcategoryRepository.save(new Subcategory(null, "Outros", leisureAndLifestyle));

                Category personalConsumption = categoryRepository.save(
                                new Category(null, "Despesa-Consumo-Pessoal", TransactionType.EXPENSE, null));

                subcategoryRepository.save(new Subcategory(null, "Roupa", personalConsumption));
                subcategoryRepository.save(new Subcategory(null, "Calcado", personalConsumption));
                subcategoryRepository.save(new Subcategory(null, "Acessorio", personalConsumption));
                subcategoryRepository.save(new Subcategory(null, "Cosmetico", personalConsumption));
                subcategoryRepository.save(new Subcategory(null, "Higiene", personalConsumption));
                subcategoryRepository.save(new Subcategory(null, "Cuidado-Pessoal", personalConsumption));
                subcategoryRepository.save(new Subcategory(null, "Outros", personalConsumption));

                Category technology = categoryRepository.save(
                                new Category(null, "Despesa-Tecnologica", TransactionType.EXPENSE, null));

                subcategoryRepository.save(new Subcategory(null, "Eletronico", technology));
                subcategoryRepository.save(new Subcategory(null, "Software", technology));
                subcategoryRepository.save(new Subcategory(null, "Gadget", technology));
                subcategoryRepository.save(new Subcategory(null, "Outros", technology));

                Category financialExpense = categoryRepository.save(
                                new Category(null, "Despesa-Financeira", TransactionType.EXPENSE, null));

                subcategoryRepository.save(new Subcategory(null, "Taxa-Bancaria", financialExpense));
                subcategoryRepository.save(new Subcategory(null, "Juros", financialExpense));
                subcategoryRepository.save(new Subcategory(null, "Imposto", financialExpense));
                subcategoryRepository.save(new Subcategory(null, "Tarifa-Cartao", financialExpense));
                subcategoryRepository.save(new Subcategory(null, "Divida", financialExpense));
                subcategoryRepository.save(new Subcategory(null, "Parcela", financialExpense));
                subcategoryRepository.save(new Subcategory(null, "Outros", financialExpense));

                Category otherRelevantExpense = categoryRepository.save(
                                new Category(null, "Outras-Despesas-Relevantes", TransactionType.EXPENSE, null));

                subcategoryRepository.save(new Subcategory(null, "Doacao", otherRelevantExpense));
                subcategoryRepository.save(new Subcategory(null, "Presente", otherRelevantExpense));
                subcategoryRepository.save(new Subcategory(null, "Pet", otherRelevantExpense));
                subcategoryRepository.save(new Subcategory(null, "Filhos", otherRelevantExpense));
                subcategoryRepository.save(new Subcategory(null, "Servico-Domestico", otherRelevantExpense));
                subcategoryRepository.save(new Subcategory(null, "Outros", otherRelevantExpense));

                Category genericExpense = categoryRepository.save(
                                new Category(null, "Despesa-Generica", TransactionType.EXPENSE, null));

                subcategoryRepository.save(new Subcategory(null, "Outros", genericExpense));

                // RECEITAS
                Category job = categoryRepository.save(
                                new Category(null, "Trabalho", TransactionType.INCOME, null));

                subcategoryRepository.save(new Subcategory(null, "Salario", job));
                subcategoryRepository.save(new Subcategory(null, "Bonus", job));
                subcategoryRepository.save(new Subcategory(null, "Comissao", job));
                subcategoryRepository.save(new Subcategory(null, "Hora-Extra", job));
                subcategoryRepository.save(new Subcategory(null, "Participacao-Nos-Lucros", job));
                subcategoryRepository.save(new Subcategory(null, "Outros", job));

                Category extraIncome = categoryRepository.save(
                                new Category(null, "Renda-Extra", TransactionType.INCOME, null));

                subcategoryRepository.save(new Subcategory(null, "Freelance", extraIncome));
                subcategoryRepository.save(new Subcategory(null, "Servico", extraIncome));
                subcategoryRepository.save(new Subcategory(null, "Venda-de-Produto", extraIncome));
                subcategoryRepository.save(new Subcategory(null, "Venda-de-Bens", extraIncome));
                subcategoryRepository.save(new Subcategory(null, "Outros", extraIncome));

                Category investments = categoryRepository.save(
                                new Category(null, "Investimentos", TransactionType.INCOME, null));

                subcategoryRepository.save(new Subcategory(null, "Dividendos", investments));
                subcategoryRepository.save(new Subcategory(null, "Juros-Recebidos", investments));
                subcategoryRepository.save(new Subcategory(null, "Rendimentos-Renda-Fixa", investments));
                subcategoryRepository.save(new Subcategory(null, "Aluguel", investments));
                subcategoryRepository.save(new Subcategory(null, "Ganho-de-Capital", investments));
                subcategoryRepository.save(new Subcategory(null, "Royalties", investments));
                subcategoryRepository.save(new Subcategory(null, "Outros", investments));

                Category otherSourcesofIncome = categoryRepository.save(
                                new Category(null, "Outras-Fontes-de-Renda", TransactionType.INCOME, null));

                subcategoryRepository.save(new Subcategory(null, "Presente", otherSourcesofIncome));
                subcategoryRepository.save(new Subcategory(null, "Heranca", otherSourcesofIncome));
                subcategoryRepository.save(new Subcategory(null, "Reembolso", otherSourcesofIncome));
                subcategoryRepository.save(new Subcategory(null, "Restituicao-de-Impostos", otherSourcesofIncome));
                subcategoryRepository.save(new Subcategory(null, "Beneficios-Governamentais", otherSourcesofIncome));
                subcategoryRepository.save(new Subcategory(null, "Outros", otherSourcesofIncome));

                Category genericSourceIncome = categoryRepository.save(
                                new Category(null, "Renda-Generica", TransactionType.INCOME, null));

                subcategoryRepository.save(new Subcategory(null, "Outros", genericSourceIncome));
        }
}
