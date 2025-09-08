package com.olympus.uga.domain.uga.presentation.dto.response;

import com.olympus.uga.domain.uga.domain.UgaAsset;
import com.olympus.uga.domain.uga.domain.enums.CharacterType;
import com.olympus.uga.domain.uga.domain.enums.ColorType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record UgaDecoRes(
        List<ColorItem> colors,
        List<CharacterItem> characters
) {
    public record ColorItem(
            ColorType colorType,
            int price,
            boolean owned
    ) {}

    public record CharacterItem(
            CharacterType characterType,
            int price,
            boolean owned
    ) {}

    public static UgaDecoRes from(UgaAsset ugaAsset) {
        List<ColorItem> colors = Arrays.stream(ColorType.values())
                .map(color -> new ColorItem(
                        color,
                        color == ColorType.DEFAULT ? 0 : 20,
                        ugaAsset.hasColor(color)
                ))
                .collect(Collectors.toList());

        List<CharacterItem> characters = Arrays.stream(CharacterType.values())
                .map(character -> new CharacterItem(
                        character,
                        character == CharacterType.UGA ? 0 : 200,
                        ugaAsset.hasCharacter(character)
                ))
                .collect(Collectors.toList());

        return new UgaDecoRes(colors, characters);
    }
}
