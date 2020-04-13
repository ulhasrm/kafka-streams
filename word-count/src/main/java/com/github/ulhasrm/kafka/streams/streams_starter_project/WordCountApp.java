package com.github.ulhasrm.kafka.streams.streams_starter_project;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

/**
 * @author Ulhas Manekar
 *
 */
public class WordCountApp
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        Properties config = new Properties();
        config.put( StreamsConfig.APPLICATION_ID_CONFIG, "word-count-app" );
        config.put( StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092" );
        config.put( ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest" );
        config.put( StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass() );
        config.put( StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass() );

        // 1 - Stream from kafka
        StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> wordCountInput = builder.stream( "word-count-input" );

        // 2 - map values to lower case
        final KTable<String, Long> wordCounts = wordCountInput.mapValues( value -> value.toLowerCase() )
            // 3 - FlatMapValues split by space
            .flatMapValues( value -> Arrays.asList( value.split( " " ) ) )
            // 4 - SelectKey to apply a key
            .selectKey( ( key, value ) -> value )
            // 5 - GroupByKey before aggregation
            .groupByKey()
            // 6 - Count occurrences in each group
            .count();

        // 7 - To in order to write the result back to kafka

        wordCounts.toStream().to( "word-count-output", Produced.with( Serdes.String(), Serdes.Long() ) );

        KafkaStreams streams = new KafkaStreams( builder.build(), config );

        final CountDownLatch latch = new CountDownLatch( 1 );

        Runtime.getRuntime().addShutdownHook( new Thread( "streams-wordcount-shutdown-hook" )
        {
            @Override
            public void run()
            {
                streams.close();
            }
        } );

        try
        {
            System.out.println( streams.toString() );
            streams.start();
            latch.await();
        }
        catch( final Throwable e )
        {
            System.exit( 1 );
        }

        System.exit( 0 );
    }
}
