package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.MPAStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class MPAService {
    private final MPAStorage mpaStorage;

    public List<MPA> getAllMPAs() {
        return mpaStorage.findAll();
    }

    public MPA getMPA(Integer id) {
        return mpaStorage.get(id);
    }
}
