cmake_minimum_required(VERSION 3.10.2)
project("rfid-lib")

add_library(
        rfid-lib
        SHARED
        src/main/cpp/rfid-lib.cpp
)

find_library(
        log-lib
        log
)

target_link_libraries(
        rfid-lib
        ${log-lib}
)