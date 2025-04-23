#include <jni.h>
#include <string>
#include <vector>
#include <stdexcept>
#include <Eigen/Dense>

using namespace Eigen;

MatrixXd jArrayToEigen(JNIEnv *env, jobjectArray jMatrix) {
    jsize rows = env->GetArrayLength(jMatrix);
    jsize cols = env->GetArrayLength((jdoubleArray)env->GetObjectArrayElement(jMatrix, 0));

    MatrixXd matrix(rows, cols);

    for (jsize i = 0; i < rows; i++) {
        jdoubleArray rowArray = (jdoubleArray)env->GetObjectArrayElement(jMatrix, i);
        jdouble *row = env->GetDoubleArrayElements(rowArray, nullptr);

        for (jsize j = 0; j < cols; j++) {
            matrix(i, j) = row[j];
        }

        env->ReleaseDoubleArrayElements(rowArray, row, 0);
    }

    return matrix;
}

jobjectArray eigenToJArray(JNIEnv *env, const MatrixXd &matrix) {
    jsize rows = matrix.rows();
    jsize cols = matrix.cols();

    // Create array of double arrays (double[][])
    jclass doubleArrayClass = env->FindClass("[D");
    jobjectArray result = env->NewObjectArray(rows, doubleArrayClass, nullptr);

    for (jsize i = 0; i < rows; i++) {
        jdoubleArray rowArray = env->NewDoubleArray(cols);
        jdouble *row = env->GetDoubleArrayElements(rowArray, nullptr);

        for (jsize j = 0; j < cols; j++) {
            row[j] = matrix(i, j);
        }

        env->ReleaseDoubleArrayElements(rowArray, row, 0);
        env->SetObjectArrayElement(result, i, rowArray);
        env->DeleteLocalRef(rowArray);
    }

    return result;
}

extern "C" {

JNIEXPORT jobjectArray JNICALL
Java_com_example_matrixcalculator_MatrixViewModel_addMatrices(
        JNIEnv *env, jobject thiz, jobjectArray matrixA, jobjectArray matrixB) {
    try {
        MatrixXd eigenA = jArrayToEigen(env, matrixA);
        MatrixXd eigenB = jArrayToEigen(env, matrixB);

        if (eigenA.rows() != eigenB.rows() || eigenA.cols() != eigenB.cols()) {
            throw std::invalid_argument("Matrices must have the same dimensions for addition");
        }

        MatrixXd result = eigenA + eigenB;
        return eigenToJArray(env, result);
    } catch (const std::exception &e) {
        return nullptr;
    }
}

JNIEXPORT jobjectArray JNICALL
Java_com_example_matrixcalculator_MatrixViewModel_subtractMatrices(
        JNIEnv *env, jobject thiz, jobjectArray matrixA, jobjectArray matrixB) {
    try {
        MatrixXd eigenA = jArrayToEigen(env, matrixA);
        MatrixXd eigenB = jArrayToEigen(env, matrixB);

        if (eigenA.rows() != eigenB.rows() || eigenA.cols() != eigenB.cols()) {
            throw std::invalid_argument("Matrices must have the same dimensions for subtraction");
        }

        MatrixXd result = eigenA - eigenB;
        return eigenToJArray(env, result);
    } catch (const std::exception &e) {
        return nullptr;
    }
}

JNIEXPORT jobjectArray JNICALL
Java_com_example_matrixcalculator_MatrixViewModel_multiplyMatrices(
        JNIEnv *env, jobject thiz, jobjectArray matrixA, jobjectArray matrixB) {
    try {
        MatrixXd eigenA = jArrayToEigen(env, matrixA);
        MatrixXd eigenB = jArrayToEigen(env, matrixB);

        if (eigenA.cols() != eigenB.rows()) {
            throw std::invalid_argument("Number of columns in first matrix must equal number of rows in second matrix");
        }

        MatrixXd result = eigenA * eigenB;
        return eigenToJArray(env, result);
    } catch (const std::exception &e) {
        return nullptr;
    }
}

JNIEXPORT jobjectArray JNICALL
Java_com_example_matrixcalculator_MatrixViewModel_divideMatrices(
        JNIEnv *env, jobject thiz, jobjectArray matrixA, jobjectArray matrixB) {
    try {
        MatrixXd eigenA = jArrayToEigen(env, matrixA);
        MatrixXd eigenB = jArrayToEigen(env, matrixB);

        if (eigenB.rows() != eigenB.cols()) {
            throw std::invalid_argument("Second matrix must be square for division");
        }

        if (eigenA.cols() != eigenB.rows()) {
            throw std::invalid_argument("Number of columns in first matrix must equal number of rows in second matrix");
        }

        MatrixXd inverseB = eigenB.inverse();
        MatrixXd result = eigenA * inverseB;

        return eigenToJArray(env, result);
    } catch (const std::exception &e) {
        return nullptr;
    }
}

} // extern "C"