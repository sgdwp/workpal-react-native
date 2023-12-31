/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

#pragma once

#include <folly/Hash.h>
#include <limits>

namespace facebook {
namespace react {

/*
 * Type used to represent an index of some stored values in small arrays.
 */
using RawPropsValueIndex = uint16_t;
static_assert(
    sizeof(RawPropsValueIndex) == 2,
    "RawPropsValueIndex must be two byte size.");
using RawPropsPropNameLength = uint16_t;
using RawPropsPropNameHash = uint32_t;

/*
 * The number which is *usually* bigger than a number of explicitly specified
 * props for some component.
 */
constexpr static int kNumberOfExplicitlySpecifiedPropsSoftCap = 16;

/*
 * Special value of `RawPropsValueIndex` which is used to represent `no value`.
 */
constexpr static RawPropsValueIndex kRawPropsValueIndexEmpty =
    std::numeric_limits<RawPropsValueIndex>::max();

/*
 * The number of props that usually more than `*Props` classes have.
 * Should be roughly equal to the number of props inside `ViewProps` plus
 * about twenty. It's okay if some `*Props` class has more than this amount of
 * props but the majority of those classes should be below this cap.
 */
constexpr static auto kNumberOfPropsPerComponentSoftCap = 150;

/*
 * The maximum length of the prop name.
 * To process prop names efficiently we have to allocate the memory statically,
 * therefore we have to have a hard limit.
 * Can be increased up to 254.
 */
constexpr static auto kPropNameLengthHardCap = 64;

} // namespace react
} // namespace facebook
