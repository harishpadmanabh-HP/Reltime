/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.accubits.reltime.activity.camera.facedetector

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.accubits.reltime.activity.camera.FacePositionDetection
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import java.util.Locale

class FaceDetectorProcessor(
    context: Context,
    detectorOptions: FaceDetectorOptions?,
    var positionDetection: FacePositionDetection
) :
    VisionProcessorBase<List<Face>>(context) {

    private val detector: FaceDetector
    private val TAG = "FaceDetectorProcessor"

    init {
        val options = detectorOptions
            ?: FaceDetectorOptions.Builder()
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .enableTracking()
                .build()

        detector = FaceDetection.getClient(options)

        Log.v(TAG, "Face detector options: $options")
    }

    override fun stop() {
        super.stop()
        detector.close()
    }

    override fun detectInImage(image: InputImage): Task<List<Face>> {
        return detector.process(image)
    }

    override fun onSuccess(faces: List<Face>, graphicOverlay: GraphicOverlay) {
        for (face in faces) {
            graphicOverlay.add(FaceGraphic(graphicOverlay, face))
            if (face != null) {
                if (face.headEulerAngleY > -12 && face.headEulerAngleY < 12) {
                    positionDetection.straightFaceDetected()
                } else if (face.headEulerAngleY < -36) {
                    positionDetection.leftFaceDetected()
                } else if (face.headEulerAngleY > 36) {
                    positionDetection.rightFaceDetected()
                }
            }
            // logExtrasForTesting(face)
        }
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Face detection failed $e")
    }

}
