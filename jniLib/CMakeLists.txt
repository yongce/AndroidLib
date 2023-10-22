project("jniLib")
cmake_minimum_required(VERSION 3.4.1)

file(GLOB inc "src/main/cpp/*.h")
file(GLOB src "src/main/cpp/*.cpp")

add_library(ycdev-commonjni
        SHARED
        ${src}
        )

include_directories(${inc})

target_link_libraries(ycdev-commonjni
        log
        android)
