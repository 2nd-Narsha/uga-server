package com.olympus.uga.domain.uga.domain;

import com.olympus.uga.domain.uga.domain.enums.CharacterType;
import com.olympus.uga.domain.uga.domain.enums.ColorType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@SuperBuilder
@Table(name = "tb_uga_asset") // 우가 자산 (소유한 색상, 캐릭터)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UgaAsset {
    @Id @Column(nullable = false)
    private String familyCode;

    @ElementCollection
    @CollectionTable(name = "tb_uga_colors")
    @Enumerated(EnumType.STRING)
    private List<ColorType> ownedColors = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "tb_uga_characters")
    @Enumerated(EnumType.STRING)
    private List<CharacterType> ownedCharacters = new ArrayList<>();

    public static UgaAsset createDefault(String familyCode) {
        List<ColorType> defaultColors = new ArrayList<>();
        defaultColors.add(ColorType.DEFAULT);

        List<CharacterType> defaultCharacters = new ArrayList<>();
        defaultCharacters.add(CharacterType.UGA);

        return UgaAsset.builder()
                .familyCode(familyCode)
                .ownedColors(defaultColors)
                .ownedCharacters(defaultCharacters)
                .build();
    }

    public void addColor(ColorType colorType) {
        if (!this.ownedColors.contains(colorType)) {
            this.ownedColors.add(colorType);
        }
    }

    public void addCharacter(CharacterType characterType) {
        if (!this.ownedCharacters.contains(characterType)) {
            this.ownedCharacters.add(characterType);
        }
    }

    public boolean hasColor(ColorType colorType) {
        return this.ownedColors.contains(colorType);
    }

    public boolean hasCharacter(CharacterType characterType) {
        return this.ownedCharacters.contains(characterType);
    }
}
