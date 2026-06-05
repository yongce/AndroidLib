cmake_minimum_required(VERSION 3.10)
project("jniLib")

file(GLOB inc "src/main/cpp/*.h")
file(GLOB src "src/main/cpp/*.cpp")

add_library(ycdev-commonjni
        SHARED
        ${src}
        )

target_link_options(
        ycdev-commonjni
        PRIVATE
        "-Wl,-z,max-page-size=16384"
)

include_directories(${inc})

target_link_libraries(ycdev-commonjni
        log
        android)
