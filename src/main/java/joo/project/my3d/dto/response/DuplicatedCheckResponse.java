package joo.project.my3d.dto.response;

public record DuplicatedCheckResponse(
        boolean duplicated
) {
    public static DuplicatedCheckResponse of(boolean duplicated) {
        return new DuplicatedCheckResponse(duplicated);
    }
}
