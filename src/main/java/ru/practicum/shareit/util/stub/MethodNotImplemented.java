package ru.practicum.shareit.util.stub;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodNotImplemented {
}
