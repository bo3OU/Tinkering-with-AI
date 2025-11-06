package com.demo.updating_brain.promptAndOutput;

import java.util.List;

public record Album(List<MusicDetails> musicList, String releaseDate, String artist) {
}
