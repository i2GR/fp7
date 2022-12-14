package run.util;

/**
 * собственное исключение для единообразия обработки
 * <p>
 * выбрасывается при ошибке загрузки сохраненных данных из файла в случаях:
 * - Ошибки формата файла
 * - ошибки формата сохраненной задачи
 * - отсутствия заголовка файла сохранения
 *
 * @see TaskSaver
 * выбрасывается также в случаях стандартных исключений java,  таких, как
 * - IllegalArgumentException
 * - NumberFormatException
 *
 */
public class ManagerLoadException extends RuntimeException {

    public ManagerLoadException(String message) {
        super(message);
    }
}
