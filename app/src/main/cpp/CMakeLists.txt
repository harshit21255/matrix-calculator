cmake_minimum_required(VERSION 3.18.0)
project(matrixoperations)

set(EIGEN_DIR ${CMAKE_CURRENT_SOURCE_DIR}/eigen-3.4.0)
include_directories(${EIGEN_DIR})

find_library(log-lib log)

add_library(matrixoperations SHARED
        matrix-operations.cpp)

target_link_libraries(matrixoperations ${log-lib})