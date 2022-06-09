import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import org.w3c.dom.ls.LSOutput;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.*;

public class StreamApi {
 /**
  *  Методы Stream API
  *
  * *************************************************************************************************************
  *  Получение объекта Stream
  *
  *  1. Пустой стрим:            Stream.empty()                // Stream<String>
  *  2. Стрим из List:           list.stream()                 // Stream <String>
  *  3. Стрим из Map:            map.entrySet().stream()       // Stream <Map.Entry <String, String>>
  *  4. Стрим из массива:        Arrays.stream(array)          // Stream <String>
  *  5. Стрим из указанными эл:  Stream.of("a", "b", "c")      // Stream <String>
  *
  *      Пример 1:
  *      public static void main(String[] args) {
  *          List <String> list = Arrays.stream(args)
  *             .filter(s -> s.length() <=2)
  *              .collect(Collectors.toList());
  *      }
  *
  *      Пример 2:
  *      IntStream.of(120, 410, 85, 32, 114, 12)
  *         .filter(x -> x < 300)
  *         .map(x -> x + 11)
  *         .limit(3)
  *         .forEach(System.out::print);
  *
  * *************************************************************************************************************
  *  Параллельные стримы
  *
  *     Пример 3:
  *     list.parallelStream()   <======
  *         .filter(x -> x>10)
  *         .map(x -> x * 2)
  *         .collect(Collectors.toList());
  *
  *     Пример 4:
  *     IntStream.range(0, 10)
  *         .parallel()    <=====
  *         .map(x -> x *10)
  *         .sum();
  *
  *  *************************************************************************************************************
  *  Стримы для примитивов
  *
  *   - IntStream()
  *   - LongStream()
  *   - DoubleStream()
  *
  * *************************************************************************************************************
  *  ИСТОЧНИКИ - Операторы Stream API (всего 10 методов)
  *
  *  1. empty() - стрим, как и коллекция может быть пустым, а значит всем последующим операторам нечего будет обрабатывать.
  *              Stream.empty()
  *                 .forEach(System.out::println);
  *
  *  2. of(T values)          - стрим для одного перечисленного элемента.
  *     of (T... values)      - стрим для нескольких перечисленных элементов.
  *              Arrays.asList(1, 2, 3).stream()
  *                 .forEach(System.out::println);
  *                                 ==
  *              Stream.of(1, 2, 3)
  *                 .forEach(System.out::println);
  *
  * 3. ofNullable (T t)  //Java9  - возвращает пустой стрим, если в качестве аргумента передан null,
  *                                 в противном случае возвращает стрим из одного элемента.
  *              String str = Math.random() > 0.5 ? "I'm feeling lucky" : null;
  *              Stream.ofNullable(str)
  *                 .forEach(System.out::println);
  *
  *  4. generate (Supplier s)  - возвращает стрим с бесконечной последовательностью єлементов (генерируемой фун-ей Supplier);
  *                              что бы не попасть в бесконечный цикл стрим нужно ограничивать функцией типа limit().
  *              Stream.generate(() -> 6)
  *                 .limit(10)
  *                 .forEach(System.out::println);
  *
  * 5. iterate (T seed, UnaryOperator f) - возвращает бесконечный стрим с элементами, которые образуются в результате
  *                                        последовательного применения функции f к итерируемому значению.
  *                                        Первый элемент будет seed, затем f(seed), затем f(f(seed)) и т.д.
  *              Stream.iterate(2, x -> x + 6)
  *                 .limit(6)
  *                 .forEach(System.out::println);
  *              // 2, 8, 14, 20, 26, 32
  *
  * 6. iterate (T seed, Predicate hasNext, UnaryOperator f) - //Java 9 - идентичный с добавлением аргумента hasNext.
  *                                                         если он возвращает false - цикл завершается. Как цикл for:
  *                                                         for (i = seed; hasNext(i); i=f(i)) {}
  *              Stream.iterate(2, x -> x < 25, x-> x+6)
  *                 .forEach (System.out::println);
  *              // 2, 8, 14, 20
  *
  * 7. concat (Stream a, Stream b)  - объединяет два стрима так, что вначале идут элементы стрима А, а потом В.
  *             Stream.concat(
  *                     Stream.of(1, 2, 3),
  *                     Stream.of(4, 5, 6))
  *                 .forEach(System.out::println);
  *
  * 8. builder()  - создает мутабельный объект для добавление элементов в стрим без использования контейнера.
  *              Stream.Builder<Integer> streamBuilder = Stream.<Integer>builder()
  *                         .add(0)
  *                         .add(1)
  *                      for (int i =5; i<=20; i+=5) {
  *                         streamBuilder.accept(i);
  *                      }
  *                 streamBuilder
  *                         .add(100)
  *                         .add(101)
  *                         .build()
  *                         .forEach(System.out::println);
  *                 // 0, 1, 5, 10, 15, 20, 100, 101
  *
  * 9.1. IntStream.range(int startInclusive, int endExclusive) - создает стрим из числового промежутка "[start..end)"!!!
  * 9.2. LongStream.range (long startInclusive, long endExclusive)
  *                 IntStream.range(0, 10)
  *                     .forEach(System.out::println);
  *                 // 0, 1, 2, 3, 4, 5, 6, 7, 8, 9
  *
  *                 LongStream.range(-10L, -5L)
  *                     .forEach(System.out::println);
  *                 // -10, -9, -8, -7, -6
  *
  * 10.1. IntStream.rangeClosed(int startInclusive, int endExclusive) - создает стрим из числового промежутка "[start..end]"!!!
  * 10.2. LongStream.rangeClosed(long startInclusive, long endInclusive) -
  *                 IntStream.rangeClosed(0, 5)
  *                     .forEach(System.out::println);
  *                 // 0, 1, 2, 3, 4, 5
  *
  *                 LongStream.range(-8L, -5L)
  *                     .forEach(System.out::println);
  *                 // -8, -7, -6, -5
  *
  * *************************************************************************************************************
  * Промежуточные операторы - Операторы Stream API (всего 11 методов)
  *
  * 1. filter(Predicate predicate) - фильтрует стрим, принимает только те элементы, которые удоволетворяет условию.
  *             Stream.of(120, 410, 85, 32, 314, 12)
  *                 .filter(x -> x > 100)
  *                 .forEach(System.out::println);
  *             // 120, 410, 314
  *
  * 2. map(Function mapper) - применяет функцию к каждому элементу и затем возвращает стрим, а также применяется для
  *                           изменения типа элемента:
  * 2.1. Stream.mapToDouble (ToDoubleFunction mapper)
  * 2.2. Stream.mapToInt (ToIntFunction mapper)
  * 2.3. Stream.mapToLong (ToLongFunction mapper)
  * 2.4. IntStream.mapToObj (IntFunction mapper)
  * 2.5. IntStream.mapToLong (IntToLongFunction mapper)
  * 2.6. IntStream.mapToDouble (IntDoubleFunction mapper)
  *            [ Stream.of("100", "200", "250")           ]           [ Stream.of("100", "200", "250")     ]
  *            |     .mapToInt(x -> Integer.parseInt(x))  |           |     .map(Integer::parseInt)        |
  *            |     .map(x -> x + 10)                    |    ==     |     .map(x -> x + 10)              |
  *            |     .forEach(System.out::println);       |           |     .forEach(System.out::println); |
  *            [ //110, 210, 260                          ]           [ //110, 210, 260                    ]
  *
  *             Stream.of("xyj", "pizda", "jopa")
  *                 .mapToInt( str -> str.length())
  *                 .forEach(System.out::println);
  *              // 3, 5, 4
  *
  * 3. flatMap(Function<T, Stream<R>> mapper) - конвертирует 2 мерный стрим/массив в 1 мерный стрим/массив
  *                         [{t, u}, {v, w, x}, {y, z}] =>Stream.flatMap() => {t, u, v, w, x, y, z}
  * 3.1. flatMapToDouble(Function mapper)
  * 3.2. flatMapToInt(Functional mapper)
  * 3.3. flatMapToLong(Function mapper)
  *
  *             Stream.of(new String[][]{{"1", "2"},{"3","4"}, {"5","6"}})
  *                 .flatMap(Stream::of)
  *                 .collect(Collectors.toList())
  *                  .forEach(System.out::print);
  *                 // 123456
  *
  *             Stream.of(new int[][]{{1, 2},{3,4}, {5,6}})
  *                  .flatMapToInt(x -> Arrays.stream(x))
  *                  .forEach(System.out::print);
  *                 //123456
  *
  *             String [][] array = new String[][]{{"a", "b"},{"c","d"}, {"e","f"}};
  *                  Stream<String[]> stream1 = Arrays.stream(array);
  *                  List<String[]> result = stream1
  *                          .filter(x -> {
  *                                 for(String s: x){
  *                                     if(s.equals("a")){
  *                                          return false;
  *                                     }
  *                                 }
  *                                 return true;
  *                          })
  *                         .collect(Collectors.toList());
  *              result.forEach(x -> System.out.println(Arrays.toString(x)));
  *             //[c, d]
  *             //[e, f]
  *
  *             Stream.of(new String[][]{{"a", "b"},{"a","d"}, {"e","a"}})
  *                     .flatMap(Stream::of)
  *                     .filter(x -> !x.equals("a"))
  *                      .collect(Collectors.toList())
  *                     .forEach(System.out::print);
  *                 //bde
  *
  * 4. limit(long maxSize) - ограничивает стрим maxSize элементами
  *             Stream.of(120, 410, 85, 32, 314, 12)
  *                 .limit(3)
  *                 .forEach(System.out::println);
  *             // 120, 410, 85
  *
  * 5. skip(long n) - пропускает n элементов стрима
  *             Stream.of(120, 410, 32, 314, 12)
  *                 .skip(3)
  *                 .forEach(System.out::println);
  *             // 314, 12
  *
  * 6. sorted(Comparator comparator) - сортирует элементы стрима.
  *             IntStream.range(0, 100)
  *                 .sorted()
  *                 .limit(3)
  *                 .forEach(System.out::println);
  *             // 0, 1, 2
  *
  * 7. distinct() - убирает повторяющиеся элементы и возвращает стрим с уникальными элементами
  *             Stream.of (2, 1, 8, 1, 3, 2)
  *                 .distinct()
  *                 .forEach(System.out::println);
  *             // 2, 1, 8, 3
  *
  * 8. peek(Consumer action) - служит для отладки кода (дебагера) - передает элемент куда-нибудь,
  *                            не разрывая при этом цепочку операторов.
  *             Stream.of("one", "two", "three", "four")
  *                 .filter(e -> e.length() > 3)
  *                 .peek(e -> System.out.print("Filter value: " + e))
  *                 .map(String::toUpperCase)
  *                 .peek(e -> System.out.print("Mapped value: "))
  *                 .collect(Collectors.toList())
  *                 .forEach(System.out::print);
  *                        //Filter value: three
  *                        //Mapped value:
  *                        //Filter value: four
  *                        //Mapped value:
  *                        //THREE
  *                        //FOUR
  *
  * 9. takeWhile(Predicate predicate) - Java9 - возвращает элементы пока они удовлетворяют условию.
  *                                     Это как оператор limit() только не с числом а с условием.
  *              Stream.of(1, 2, 3, 4, 2, 5)
  *                 .takeWhile(x -> x < 3)
  *                 .forEach(System.out::println)
  *              // 1, 2 - вторая '2' не попадает!!!
  *
  * 10. dropWhile(Predicate predicate) - Java9 - пропускает элементы пока они удовлетворяют условию, затем
  *                                      возвращает оставшуюся часть стрима.
  *                                      Подобен оператору skip(), только работает по условию.
  *             Stream.of(1, 2, 3, 4, 2, 5)
  *                 .dropWhile(x -> x < 3)
  *                 .forEach(System.out::println);
  *             // 3, 4, 2, 5
  *
  * 11. boxed () - преобразует примитивный стрим в объектный. Выполняется только с:
  *                IntStream / DoubleStream / LongStream
  *                      DoubleStream.of( 1 , 0.7 , Math.PI)
  *                          .boxed()
  *                          .map(Object::getClass)
  *                          .forEach (System.out::println);
  *                   //class java.lang.Double
  *                   //class java.lang.Double
  *                   //class java.lang.Double
  *             Если без этого метода???
  *                      Stream.of ( 1 , 0.7, Math.PI)
  *                         .map(Object::GetClass)
  *                         .forEach(System.out::println)
  *                   //class java.lang.Integer
  *                   //class java.lang.Double
  *                   //class java.lang.Double
  *
  * *************************************************************************************************************
  * Терминальные операторы - Операторы Stream API (всего 11 операторов)
  *
  * 1.void forEach() - выполняет указанное действие для каждого элемента стрима.
  *                        Stream.of(120, 410, 85, 32, 314, 12)
  *                          .forEach(x -> System.out.format("%s, ", x));
  *                       // 120, 410, 85, 32, 314, 12
  *
  * 2. void forEachOrdered(Consumer action) - выполняет указанное действие для каждого элемента стрима, но
  *                                           перед этим добивается правильного порядка вхождения элементов.
  *                                           Используется для параллельных стримов, когда нужно получить
  *                                           правильную последовательность элементов.
  *                [ IntStream.range(0, 50)             ]           [ IntStream.range(0, 50)                      ]
  *                |     .parallel()                    |           |      .parallel()                            |
  *                |     .filter(x -> x%10 == 0)        |     VS    |      .filter(x -> x%10 == 0)                |
  *                |     .map(x -> x / 10)              |           |      .map(x -> x / 10)                      |
  *                |     .forEach(System.out::println); |   <<==>>  |      .forEachOrdered(System.out::println);  |
  *                [  // 3, 0, 1, 5, 4, 2               ]           [   // 0, 1, 2, 3, 4                          ]
  *
  * 3. long count() - возвращает количество элементов стрима.
  *                 long x = Stream.of(10, 20, 30, 40)
  *                     .count();
  *                  System.out.println(x);
  *                  // 4
  *
  *                  long x = Stream.of("one", "two", "three")
  *                     .count();
  *                  System.out.println(x);
  *                  // 3
  *
  * 4. R collect (Collector collector) - с его помощью можно собрать все элементы в список, множество,
  *                                      или другую коллекцию, сгруппировать элементы по какому-нибудь
  *                                      критерию, объединить все в одну строку и т.д.
  *                 List <Integer> list = Stream.of(1, 2, 3)
  *                         .collect(Collectors.toList());
  *                 System.out.println(list);
  *                 // 1, 2, 3
  *
  *                 String s = Stream.of (1, 2, 3)
  *                         .map(String::valueOf)
  *                         .collect(Collectors.joining("-", "<", ">"));
  *                 System.out.println(s);
  *                 //<1-2-3>
  *
  * 5. R collect(Supplier supplier, BiConsumer accumulator, BiConsumer combiner) - тоже, что и collector(), только:
  *                                 supplier -    должен поставлять новые объекты/контейнеры, например new ArrayList();
  *                                 accumulator - добавляет элемент в контейнер;
  *                                 combiner -    нужен для параллельных стримов и объединяет части стримов воедино.
  *                 List<String> list = Stream.of("a", "b", "c", "d")
  *                         .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
  *                 System.out.println(list);
  *                 // [a, b, c, d]
  *
  * 6.1. Object[] toArray() - возвращает не типизированный массив с элементами стрима.
  * 6.2, A[] toArray(IntFunction<A[]> generator) - возвращает типизированный массив с элементами стрима.
  *                 Sting[] elements = Stream.of("a", "b", "c", "d")
  *                         .toArray(String[]::new);
  *                 System.out.println(elements);
  *                 // ["a", "b", "c", "d"]
  *
  * 7.1. T reduce(T identity, BinaryOperator accumulator)
  * 7.2. U reduce(U identity, BiFunction accumulator, BinaryOperator combiner)
  *                             Позволяет преобразовать все элементы стрима в один объект.
  *                             Например посчитать сумму всех элементов, или найти минимальный элемент.
  *                 int sum = Stream.of(1, 2, 3, 4, 5)
  *                        .reduce(10, (acc, x) -> acc + x);
  *                 System.out.println(sum);
  *                 // 25   ===> (10+1+2+3+4+5)
  *
  *                 int min = Stream.of(1, 2, 4, 7, 9)
  *                         .reduce(Integer.MAX_VALUE, (left, right) -> left < right ? left : right);
  *                 System.out.println(min);
  *                 // 1
  * 7.3. Optional reduce(BinaryOperator accumulator) - отличается тем, что у него нет начального элемента identity.
  *                             В качестве него служит первый элемент стрима.
  *                             Если стрим пустой - тогда вернется Optional.empty().
  *                 Optional<Integer> sum = Stream.of(1, 2, 3, 4, 5)
  *                          .reduce((acc, x) -> acc + x);
  *                 System.out.println(sum.get());
  *                 // 15  => (1+2+3+4+5)
  *
  *                 Optional <Integer> min = Stream.of( 2, 7, 3, 4, 5 ,6)
  *                     .reduce((left, right) -> left < right ? left : right);
  *                 System.out.println(min.get());
  *                 // 2
  *
  * 8.1. Optional min(Comparator comparator) - поиск минимального значения
  *                 int min = Stream.of(20, 11, 45, 78, 13)
  *                      .min(Integer::compare).get();
  *                 // 11
  *
  *                 String product = Stream.of("one", "two", "one-one", "four", "four-four")
  *                         .min(String::indexOf).get();
  *                 System.out.println(product);
  *                     //one - 'two' не в счет :(
  *
  * 8.2. Optional max(Comparator comparator) - поиск максимального значения
  *                 int max = Stream.of(20, 11, 45, 78, 13)
  *                      .max(Integer::compare).get();
  *                 // 78
  *
  * 9.1. Optional findAny() - возвращает первый попавшийся элемент параллельного стрима.
  *                           По обычному стриму выдает первый элемент.
  *                 String product = Stream.of("one", "two", "one-one", "four", "four-four")
  *                         .findAny().get();
  *                 System.out.println(product);
  *                 //one
  *
  * 9.2. Optional findFirst() - гарантировано возвращает первый элемент стрима, даже если стрим параллельный.
  *                 int product = IntStream.of(5, 4, 3, 2, 1, 0)
  *                         .findFirst()
  *                         .getAsInt();
  *                 // 5
  *
  * 10.1. boolean allMatch(Predicate predicate) - возвращает true, если все элементы стрима удовлетворяют условию
  *                             predicate. Если один из элементов стрима не проходи по условию - будет false.
  *                 boolean result = Stream.of(1, 2, 3, 4, 5)
  *                         .allMatch(x -> x <= 7)
  *                 // true
  *
  * 10.2. boolean anyMatch(Predicate predicate) - возвращает true, если хотя бы один элемент стрима удовлетворяет
  *                                              условию predicate.
  *                  boolean result = Stream.of(1, 2, 3, 4, 5)
  *                         .anyMatch(x -> x == 3);
  *                  // true
  *
  * 10.3. boolean noneMatch(Predicate predicate) - возвращает true, если, пройдя все элементы стрима ни один не
  *                                                удовлетворил условию predicate.
  *                 boolean result = Stream.of(1, 2, 3, 4, 5)
  *                         .noneMatch(x -> x % 2 == 1)
  *                 // true - ни один не совпал - все четные
  *
  * 11.1. OptionalDouble average() - только для примитивных стримов. Возвращает среднее арифметическое всех элементов.
  *                                  Либо Optional.empty(), если стрим пуст.
  *                  double result = IntStream.range(2, 15)
  *                             .average()
  *                             .getAsDouble();
  *                 // 8.5
  *
  * 11.2. sum() - возвращает сумму элементов примитивного стрима.
  *               Для IntStream будет тип int, LongStream - long, DoubleString - double
  *                     long result = LongStream.range(2, 16)
  *                              .sum();
  *                     // 119
  *
  * 11.3. IntSummaryStatistics summaryStatistics() - собирает статистику о числовой последовательности стрима, а именно:
  *                                                 а) количество элементов
  *                                                 б) сумма
  *                                                 в) среднее арифметическое
  *                                                 г) минимальный элемент
  *                                                 д) максимальный элемент
  *                     LongSummaryStatistics stats = LongStream.range(2, 16)
  *                              .summaryStatistics();
  *                     System.out.println("Count: " + stats.getCount());
  *                     System.out.println("Sum: " + stats.getSum());
  *                     System.out.println("Average: " + stats.getAverage());
  *                     System.out.println("Min: " + stats.getMin());
  *                     System.out.println("Max: " + stats.getMax());
  *                     //Count: 14
  *                     //Sum: 119
  *                     //Average: 8.5
  *                     //Min: 2
  *                     //Max: 15
  *
  * *************************************************************************************************************
  * Методы Collectors (всего 15 шт)
  *
  * 1. toList() - собирает элементы в List.
  *
  * 2. toSet() - собирает элементы в множество.
  *
  * 3. toCollection (Supplier collectionFactory) - собирает элементы в заданную коллекцию. Если нужно конкретно указать,
  *                                                какой List, Set или другую коллекцию мы хотим использовать.
  *                 Deque<Integer> deque = Stream.of(1, 2, 3, 4, 5)
  *                         .collect(Collectors.toCollection(ArrayDeque::new));
  *
  *                 Set<Integer> set = Stream.of(1, 2, 3, 4, 5)
  *                         .collect(Collectors.toCollection(LinkedHashSet::new));
  *
  * 4.1. toMap(Function keyMapper, Function valueMapper) - собирает элементы в Map. Каждый элемент преобразовывется в
  *                                 ключ и значение, основываясь на результате функции keyMapper и valueMapper.
  *                                 Если нужно вернуть тот же элемент, что и пришел, стоит передать Functional.identity().
  *                  Map<Integer, Integer> map1 = Steam.of(1, 2, 3, 4, 5)
  *                         .collect(Collectors.toMap(
  *                             Function.identity(),
  *                             Function.identity() ));
  *                 // {1=1, 2=2, 3=3, 4=4, 5=5}
  *
  *                 Map<Integer, String> map2 = Stream.of(1, 2, 3)
  *                         .collect(Collectors.toMap(
  *                             Function.identity(),
  *                             i -> String.format("%d * 2 = %d", i, i * 2) ));
  *                  // {1 = "1 * 2 = 2", 2 = "2 * 2 = 4", 3 = "3 * 2 = 6"}
  *
  *                 Map <Character, String> map3 = Stream.of(50, 54, 55)
  *                      .collect(Collectors.toMap(
  *                          i -> (char) i.intValue(),
  *                          i -> String.format("<%d>", i)  ));
  *                 System.out.println(map3);
  *                 // {2=<50>, 6=<54>, 7=<55>}
  *
  * 4.2. toMap(Function keyMapper, Function valueMapper, BinaryOperator mergeFunction) - аналогичен первой версии
  *                                 метода, только в случае, когда встречается два одинаковых ключа, позволяет
  *                                 объеденить значения values!
  *                 Map<Integer, String> map4 = Stream.of(50,50, 41, 50, 41, 20, 13, 20, 13, 18)
  *                     .collect(Collectors.toMap(
  *                             i -> i % 5,
  *                             i -> String.format("<%d>", i),
  *                             (a, b) -> String.join(",", a, b)
  *                     ));
  *                 System.out.println(map4);
  *                 // {0=<50>,<50>,<50>,<20>,<20>, 1=<41>,<41>, 3=<13>,<13>,<18>}
  *
  * 4.3. toMap(Function keyMapper, Function valueMapper, BinaryOperator mergeFunction, Supplier mapFactory) -
  *                                 идентично предыдущему оператору, с возможностью указать какой класс Map использовать.
  *                 Map<Integer, String> map5 = Stream.of(50,50, 41, 50, 41, 20, 13, 20, 13, 18)
  *                     .collect(Collectors.toMap(
  *                             i -> i % 5,
  *                             i -> String.format("<%d>", i),
  *                             (a, b) -> String.join(",", a, b),
  *                             LinkedHashMap::new
  *                     ));
  *
  * 5.1. toConcurrentMap(Function keyMapper, Function valueMapper) - concurrent это параллель.
  *                                 Идентично toMap, только работаем с ConcurrentMap (потокобезопасная карта)
  *
  * 5.2. toConcurrentMap(Function keyMapper, Function valueMapper, BinaryOperator mergeFunction) -
  *                                 см. п.4.2.(+ потокобезопасная карта)
  *
  * 5.3. toConcurrentMap(Function keyMapper, Function valueMapper, BinaryOperator mergeFunction, Supplier mapFactory) -
  *                                 cм. п. 4.3.(+ потокобезопасная карта)
  *
  * 6. collectingAndThen(Collector downstream, Function finisher) - собирает элементы с помощью указанного коллектора,
  *                                а потом применяет к полученому результату функции.
  *
  *               List<Integer> list = Stream.of(1, 2, 3, 4, 5)
  *                     .collect(Collectors.collectingAndThen(
  *                             Collectors.toList(),
  *                             Collections::unmodifiableList));
  *             System.out.println(list.getClass());
  *             //class java.util.Collections$UnmodifiableRandomAccessList
  *
  *               ArrayList list = Stream.of(1, 2, 3, 1, 9, 2, 5, 3, 4, 8, 2)
  *                       .collect(Collectors.collectingAndThen(
  *                               Collectors.toCollection(LinkedHashSet::new),
  *                               ArrayList::new
  *                       ));
  *               System.out.println(list);
  *               // [1, 2, 3, 9, 5, 4, 8]
  *
  * 7.1. joining()
  * 7.2. joining(CharSequence delimiter)
  * 7.3. joining(CharSequence delimiter, CharSequence prefix, CharSequence syffix)
  *                                 Собирает элементы в эдиную строку. Дополнительно можно указать разделитель,
  *                                 а также префикс и суфикс для всей последовательности.
  *                String s7_1 = Stream.of("a", "b", "c", "d")
  *                     .collect(Collectors.joining());
  *                System.out.println(s7_1);
  *                // abcd
  *
  *                String s7_2 = Stream.of("a", "b", "c", "d")
  *                     .collect(Collectors.join("-"));
  *                System.out.println(s7_2);
  *                //a-b-c-d
  *
  *                String s7_3 = Stream.of("a", "b", "c", "d")
  *                     .collect(Collectors.join(" => ", "[ ", " ]");
  *                System.out.println(s7_3);
  *                // [ a => b => c => d ]
  *
  * 8.1. summingInt(ToIntFunction mapper)
  * 8.2. summingLong(ToLongFunction mapper)
  * 8.3. summingDouble(ToDoubleFunction mapper)
  *                               Коллектор, который преобразовывает обьекты в int/long/double и подсчитывает сумму.
  *                 Integer sum = Stream.of("1", "2", "3", "4")
  *                         .collect(Collectors.summingInt(Integer::parseInt));
  *                 System.out.println(sum);
  *                 // 10
  * 9.1. averagingInt(ToIntFunction mapper)
  * 9.2. averagingLong(ToLongFunction mapper)
  * 9.3. averagingDouble(ToDoubleFunction mapper)
  *                               Коллектор, который преобразовывает объекты в int/long/double и подсчитывает среднее.
  *                  Double average = Stream.of("1", "2", "3", "4")
  *                         .collect(Collectors.averagingDouble(Double::parseDouble));
  *                  System.out.println(average);
  *                  //2.5
  *
  * 10.1. summarizingInt(ToIntFunction mapper)
  * 10.2. summarizingLong(ToLongFunction mapper)
  * 10.3. summarizingDouble(ToDoubleFunction mapper)
  *                 DoubleSummaryStatistics stats = Stream.of("1.1", "1.23", "3.23")
  *                     .collect(Collectors.summarizingDouble(Double::parseDouble));
  *                 System.out.println(stats);
  *                 //DoubleSummaryStatistics{count=3, sum=5,560000, min=1,100000, average=1,853333, max=3,230000}
  *
  * 11.1. counting() - подсчитывает количество элементов
  *                 Long count = Stream.of("1", "2", "3", "4")
  *                     .collect(Collectors.counting());
  *                 System.out.println(count);
  *                 // 4
  *
  * 12.1. minBy(Comparator comparator)
  * 12.2. maxBy(Comparator comparator)
  *                             Поиск минимального/максимального элемента, основываясь на заданном компараторе.
  *                Optional<String> str = Stream.of("abra", "kadabra", "zelenuj", "ogyrez")
  *                       .collect(Collectors.minBy(Comparator.comparing(String::length)));
  *               str.ifPresent(System.out::println);
  *               // abra
  *
  *               Optional<String> str = Stream.of("raz", "desyat", "vzyali", "potyanyli")
  *                       .collect(Collectors.maxBy(Comparator.comparing(String::length)));
  *               System.out.println(str.get());
  *               // potyanyli
  *
  * 13.1. groupingBy(Functional classifier)
  * 13.2. groupingBy(Functional classifier, Collector downstream)
  * 13.3. groupingBy(Functional classifier, Supplier mapFactory, Collector downstream)
  *               Map<Integer, List<String>> map1 = Stream.of("ab", "a", "bdc", "lf", "w")       //!!!List<String>
  *                       .collect(Collectors.groupingBy(String::length));
  *               map1.entrySet().forEach(System.out::println);
  *               System.out.println(map1);
  *               //1=[a, w]
  *               //2=[ab, lf]
  *               //3=[bdc]
  *               //{1=[a, w], 2=[ab, lf], 3=[bdc]}
  *
  *               Map<Integer, String> map2 = Stream.of("ab", "a", "bdc", "lf", "w")            //!!!!!String
  *                       .collect(Collectors.groupingBy(
  *                               String::length,
  *                               Collectors.mapping(
  *                                       String::toUpperCase,
  *                                       Collectors.joining())
  *                       ));
  *               map2.entrySet().forEach(System.out::println);
  *               System.out.println(map2);
  *               //1=AW
  *               //2=ABLF
  *               //3=BDC
  *               //{1=AW, 2=ABLF, 3=BDC}
  *
  *               Map<Integer, List<String>> map3 = Stream.of(
  *                       "ab", "a", "bdc", "lf", "w")
  *                       .collect(Collectors.groupingBy(
  *                               String::length,
  *                               LinkedHashMap::new,
  *                               Collectors.mapping(
  *                                       String::toUpperCase,
  *                                       Collectors.toList())
  *                               ));
  *
  *                  map3.entrySet().forEach(System.out::println);
  *                  System.out.println(map3);
  *                  // 2=[AB, LF]
  *                  //1=[A, W]
  *                  //3=[BDC]
  *                 //{2=[AB, LF], 1=[A, W], 3=[BDC]}
  *
  * 14.1. groupingByConcurrent(Function classifier) - см. п. 13.1. (только для параллели)
  * 14.2. groupingByConcurrent(Functional classifier, Collector downstream) - см. п. 13.2. (только для параллели)
  * 14.3. groupingByConcurrent(Functional classifier, Supplier mapFactory, Collector downstream) - см. п. 13.3. (только для параллели)
  *
  * 15.1. partitioningBy(Predicate predicate)
  * 15.2. partitioningBy(Predicate predicate, Collector downstream) - разбивает последовательность элементов по
  *                                          какому-либо критерию. В одну часть попадают все элементы, которые
  *                                          удовлетворяют переданному условию, во вторую - все остальное.
  *                    Map<Boolean, List<String>> map1 = Stream.of(
  *                       "ab", "c", "def", "gh", "ijk", "1", "mnop")
  *                       .collect(Collectors.partitioningBy( s -> s.length() <= 2));
  *                   map1.entrySet().forEach(System.out::println);
  *                   // false=[def, ijk, mnop]
  *                   // true=[ab, c, gh, 1]
  *
  *                   Map<Boolean, String> map2 = Stream.of(
  *                       "ab", "c", "def", "gh", "ijk", "1", "mnop")
  *                       .collect(Collectors.partitioningBy(
  *                               s -> s.length() <= 2,
  *                               Collectors.mapping(
  *                                       String::toUpperCase,
  *                                       Collectors.joining()
  *                               )
  *                               ));
  *                  map2.entrySet().forEach(System.out::println);
  *                  // false=DEFIJKMNOP
  *                  // true=ABCGH1
  *
  * *************************************************************************************************************

  *
*/



 public static void main(String[] args) {


            List<Integer> list = Stream.of(1, 2, 3, 4, 5)
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            Collections::unmodifiableList));
            System.out.println(list.getClass());
 }

}


