package ru.inno.course.player.myTests.resolvers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.helpers.AnnotationHelper;
import ru.inno.course.player.myTests.annotations.GeneratePlayers;
import ru.inno.course.player.myTests.ext.PlayerGenerator;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class TestDataResolver implements ParameterResolver, AfterEachCallback {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(PlayerService.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        GeneratePlayers annotation = AnnotationHelper.findAnnotation(extensionContext.getRequiredTestMethod(), GeneratePlayers.class);
        if (annotation == null) {
            return new PlayerServiceImpl();
        }

        int num = annotation.value();
        Set<Player> generated = PlayerGenerator.generate(num);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(Path.of("./data.json").toFile(), generated);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new PlayerServiceImpl();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        Files.deleteIfExists(Path.of("./data.json"));
    }
}
