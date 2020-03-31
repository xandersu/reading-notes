package com.xandersu.readingnotes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Subscription;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReadingNotesApplicationWebfluxTest {


    @Test
    public void contextLoads() {
    }

    @Test
    public void testWebflux() {
        Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5, 6);
        Mono<Integer> mono = Mono.just(1);

        Integer[] integers = {1, 2, 3, 4, 5, 6};
        Flux<Integer> arrayFlux = Flux.fromArray(integers);

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
        Flux<Integer> listFlux = Flux.fromIterable(list);

        Flux<Integer> fluxFlux = Flux.from(flux);

        Flux<Integer> streamFlux = Flux.fromStream(Stream.of(1, 2, 3, 4, 5, 6, 7));

//        flux.subscribe();
//        arrayFlux.subscribe(System.out::println);
//        listFlux.subscribe(System.out::println, System.err::println);
//        fluxFlux.subscribe(System.out::println, System.err::println, () -> System.out.println("complete"));
//        streamFlux.subscribe(System.out::println, System.err::println, () -> System.out.println("complete"), subscription -> subscription.request(3));

//        streamFlux.subscribe(new DemoSubscribe());

//        flux.map(i -> i * 3).subscribe(System.out::println);
//        System.out.println("1---------------------");
//        arrayFlux.flatMap(i -> flux).subscribe(System.out::println);
//        System.out.println("2---------------------");
//        listFlux.filter(i -> i > 3).subscribe(System.out::println);
//        System.out.println("3---------------------");
//        Flux.zip(fluxFlux, streamFlux).subscribe(zip -> System.out.println(zip.getT1() + zip.getT2()));
//        System.out.println("4---------------------");

        flux.map(i -> {
            System.out.println(Thread.currentThread().getName() + " --map1");
            return i * 3;
        })
                .publishOn(Schedulers.elastic())
                .map(i -> {
                    System.out.println(Thread.currentThread().getName() + " --map2");
                    return i / 3;
                })
//                .subscribeOn(Schedulers.parallel())
                .subscribe(it -> System.out.println(Thread.currentThread().getName() + " --map3 --it= " + it));

//        while(true){
//
//        }
    }

    class DemoSubscribe extends BaseSubscriber<Integer> {
        @Override
        protected void hookOnSubscribe(Subscription subscription) {
//            super.hookOnSubscribe(subscription);
            System.out.println("subscribe");
            subscription.request(1);

        }

        @Override
        protected void hookOnNext(Integer value) {
            if (value == 4) {
                cancel();
            }
            System.out.println(value);
            request(1);
        }
    }


}
