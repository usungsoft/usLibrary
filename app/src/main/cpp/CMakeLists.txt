cmake_minimum_required(VERSION 3.10.2)
project("rfidLib")

add_library(
        ${CMAKE_PROJECT_NAME}
        SHARED
        rfidLib.cpp
)

find_library(
        log-lib
        log
)

target_link_libraries(
        ${CMAKE_PROJECT_NAME}
        android
        log
)