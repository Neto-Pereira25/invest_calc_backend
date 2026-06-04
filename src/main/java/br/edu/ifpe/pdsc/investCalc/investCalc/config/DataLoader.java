package br.edu.ifpe.pdsc.investCalc.investCalc.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

                Map<String, Category> categories = seedCategories();
                seedSubcategories(categories);
        }

        private Map<String, Category> seedCategories() {
                Map<String, Category> categoriesByKey = new HashMap<>();

                categoryRepository.findAll().forEach(
                                category -> categoriesByKey.put(categoryKey(category.getName(), category.getType()), category));

                ensureCategory(categoriesByKey, "Despesa-Moradia", TransactionType.EXPENSE);
                ensureCategory(categoriesByKey, "Despesa-Servico-Essencial", TransactionType.EXPENSE);
                ensureCategory(categoriesByKey, "Despesa-Alimentacao", TransactionType.EXPENSE);
                ensureCategory(categoriesByKey, "Despesa-Transporte", TransactionType.EXPENSE);
                ensureCategory(categoriesByKey, "Despesa-Saude", TransactionType.EXPENSE);
                ensureCategory(categoriesByKey, "Despesa-Educacao", TransactionType.EXPENSE);
                ensureCategory(categoriesByKey, "Despesa-Lazer-Estilo-de-Vida", TransactionType.EXPENSE);
                ensureCategory(categoriesByKey, "Despesa-Consumo-Pessoal", TransactionType.EXPENSE);
                ensureCategory(categoriesByKey, "Despesa-Tecnologica", TransactionType.EXPENSE);
                ensureCategory(categoriesByKey, "Despesa-Financeira", TransactionType.EXPENSE);
                ensureCategory(categoriesByKey, "Outras-Despesas-Relevantes", TransactionType.EXPENSE);
                ensureCategory(categoriesByKey, "Despesa-Generica", TransactionType.EXPENSE);

                ensureCategory(categoriesByKey, "Trabalho", TransactionType.INCOME);
                ensureCategory(categoriesByKey, "Renda-Extra", TransactionType.INCOME);
                ensureCategory(categoriesByKey, "Investimentos", TransactionType.INCOME);
                ensureCategory(categoriesByKey, "Outras-Fontes-de-Renda", TransactionType.INCOME);
                ensureCategory(categoriesByKey, "Renda-Generica", TransactionType.INCOME);

                return categoriesByKey;
        }

        private void seedSubcategories(Map<String, Category> categoriesByKey) {
                ensureSubcategories(categoriesByKey, "Despesa-Moradia", TransactionType.EXPENSE, "Aluguel", "Condominio", "IPTU",
                                "Energia", "Agua", "Gas", "Manutencao-Residencial", "Reforma", "Moveis-Decoracao", "Outros");

                ensureSubcategories(categoriesByKey, "Despesa-Servico-Essencial", TransactionType.EXPENSE, "Internet",
                                "Telefone-Movel", "Planos-Seguros", "Outros");

                ensureSubcategories(categoriesByKey, "Despesa-Alimentacao", TransactionType.EXPENSE, "Supermercado", "Restaurante",
                                "Delivery", "Cafeteria-Lanche", "Outros");

                ensureSubcategories(categoriesByKey, "Despesa-Transporte", TransactionType.EXPENSE, "Combustivel",
                                "Transporte-Publico", "Uber", "Manutencao-Veicular", "Seguro-Veicular", "Estacionamento",
                                "Pedagio", "Multas", "Outros");

                ensureSubcategories(categoriesByKey, "Despesa-Saude", TransactionType.EXPENSE, "Plano-Saude", "Farmacia",
                                "Consultas", "Exames", "Terapia", "Psicologia", "Odontologia", "Outros");

                ensureSubcategories(categoriesByKey, "Despesa-Educacao", TransactionType.EXPENSE, "Mensalidade-Escola",
                                "Mensalidade-Universidade", "Cursos", "Livros", "Material-Escolar");

                ensureSubcategories(categoriesByKey, "Despesa-Lazer-Estilo-de-Vida", TransactionType.EXPENSE, "Streaming", "Cinema",
                                "Evento", "Hobbies", "Viagem", "Hotel", "Passeio", "Outros");

                ensureSubcategories(categoriesByKey, "Despesa-Consumo-Pessoal", TransactionType.EXPENSE, "Roupa", "Calcado",
                                "Acessorio", "Cosmetico", "Higiene", "Cuidado-Pessoal", "Outros");

                ensureSubcategories(categoriesByKey, "Despesa-Tecnologica", TransactionType.EXPENSE, "Eletronico", "Software",
                                "Gadget", "Outros");

                ensureSubcategories(categoriesByKey, "Despesa-Financeira", TransactionType.EXPENSE, "Taxa-Bancaria", "Juros",
                                "Imposto", "Tarifa-Cartao", "Divida", "Parcela", "Outros");

                ensureSubcategories(categoriesByKey, "Outras-Despesas-Relevantes", TransactionType.EXPENSE, "Doacao", "Presente",
                                "Pet", "Filhos", "Servico-Domestico", "Outros");

                ensureSubcategories(categoriesByKey, "Despesa-Generica", TransactionType.EXPENSE, "Outros");

                ensureSubcategories(categoriesByKey, "Trabalho", TransactionType.INCOME, "Salario", "Bonus", "Comissao",
                                "Hora-Extra", "Participacao-Nos-Lucros", "Outros");

                ensureSubcategories(categoriesByKey, "Renda-Extra", TransactionType.INCOME, "Freelance", "Servico",
                                "Venda-de-Produto", "Venda-de-Bens", "Outros");

                ensureSubcategories(categoriesByKey, "Investimentos", TransactionType.INCOME, "Dividendos", "Juros-Recebidos",
                                "Rendimentos-Renda-Fixa", "Aluguel", "Ganho-de-Capital", "Royalties", "Outros");

                ensureSubcategories(categoriesByKey, "Outras-Fontes-de-Renda", TransactionType.INCOME, "Presente", "Heranca",
                                "Reembolso", "Restituicao-de-Impostos", "Beneficios-Governamentais", "Outros");

                ensureSubcategories(categoriesByKey, "Renda-Generica", TransactionType.INCOME, "Outros");
        }

        private void ensureCategory(Map<String, Category> categoriesByKey, String name, TransactionType type) {
                String key = categoryKey(name, type);
                if (categoriesByKey.containsKey(key)) {
                        return;
                }

                Category category = categoryRepository.save(new Category(null, name, type, null));
                categoriesByKey.put(key, category);
        }

        private void ensureSubcategories(Map<String, Category> categoriesByKey, String categoryName, TransactionType type,
                        String... expectedSubcategories) {
                Category category = categoriesByKey.get(categoryKey(categoryName, type));
                if (category == null) {
                        return;
                }

                List<String> existingSubcategoryNames = subcategoryRepository.findByCategory(category)
                                .stream()
                                .map(Subcategory::getName)
                                .toList();

                Arrays.stream(expectedSubcategories)
                                .filter(subcategoryName -> !existingSubcategoryNames.contains(subcategoryName))
                                .forEach(subcategoryName ->
                                                subcategoryRepository.save(new Subcategory(null, subcategoryName, category)));
        }

        private String categoryKey(String name, TransactionType type) {
                return type.name() + "::" + name;
        }
}
