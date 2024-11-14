import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;
class Foo {
    public User methodA(List<Item> loads) {
        List<SummaryDto.ItemDto> items = new ArrayList<>();
        loads.stream()
             .collect(Collectors.groupingBy(Item::getValue))
             .forEach((a, b) -> items.add(buildItem(a, b)));
    }

    private SummaryDto.ItemDto buildItem(BigDecimal a, List<Item> b) {
        return SummaryDto.ItemDto.builder().build();
    }
}