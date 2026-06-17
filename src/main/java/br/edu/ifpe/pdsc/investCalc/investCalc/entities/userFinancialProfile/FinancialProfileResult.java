package br.edu.ifpe.pdsc.investCalc.investCalc.entities.userFinancialProfile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.userFinancialProfile.FinancialProfile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "financial_profile_results", indexes = {
        @Index(name = "idx_profile_user", columnList = "user_id"),
        @Index(name = "idx_profile_assessed_at", columnList = "assessed_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialProfileResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FinancialProfile profile;

    @Column(nullable = false)
    private Integer devedorScore;

    @Column(nullable = false)
    private Integer gastadorScore;

    @Column(nullable = false)
    private Integer desligadoScore;

    @Column(nullable = false)
    private Integer poupadorScore;

    @Column(nullable = false)
    private Integer investidorScore;

    @Column(nullable = false)
    private LocalDateTime assessedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FinancialProfileAnswer> answers = new ArrayList<>();

}
