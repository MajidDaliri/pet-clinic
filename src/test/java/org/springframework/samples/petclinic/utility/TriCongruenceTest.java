package org.springframework.samples.petclinic.utility;

import com.github.mryf323.tractatus.*;
import com.github.mryf323.tractatus.experimental.extensions.ReportingExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ReportingExtension.class)
@ClauseDefinition(clause = 'a', def = "t1A != t2A")
@ClauseDefinition(clause = 'b', def = "t1B != t2B")
@ClauseDefinition(clause = 'c', def = "t1C != t2C")
@ClauseDefinition(clause = 'd', def = "t1A < 0")
@ClauseDefinition(clause = 'e', def = "t1A + t1B < t1C")
class TriCongruenceTest {

	private static final Logger log = LoggerFactory.getLogger(TriCongruenceTest.class);

	/**
	 * Predicate: a + b + c
	 * Order a, b, c
	 * a:
	 * 	UTP : {TFF}
	 * 	NFP : {FFF}
	 * b:
	 * 	UTP : {FTF}
	 * 	NFP : {FFF}
	 * c:
	 * 	UTP : {FFT}
	 * 	NFP : {FFF}
	 *
	 * CUTPNFP : {TFF, FTF, FFT, FFF}
	 */
	public boolean predicate14(double t1A, double t1B, double t1C, double t2A, double t2B, double t2C) {
		assert t1A < t1B && t1B < t1C;
		assert t2A < t2B && t2B < t2C;
		return t1A != t2A || t1B != t2B || t1C != t2C;
	}

	@UniqueTruePoint(
		predicate = "a + b + c",
		dnf = "a + b + c",
		implicant = "a",
		valuations = {
			@Valuation(clause = 'a', valuation = true),
			@Valuation(clause = 'b', valuation = false),
			@Valuation(clause = 'c', valuation = false)
		}
	)
	@Test
	public void test1() {
		assertTrue(predicate14(1, 3, 4, 2, 3, 4));
	}

	@UniqueTruePoint(
		predicate = "a + b + c",
		dnf = "a + b + c",
		implicant = "b",
		valuations = {
			@Valuation(clause = 'a', valuation = false),
			@Valuation(clause = 'b', valuation = true),
			@Valuation(clause = 'c', valuation = false)
		}
	)
	@Test
	public void test2() {
		assertTrue(predicate14(1, 2, 4, 1, 3, 4));
	}

	@UniqueTruePoint(
		predicate = "a + b + c",
		dnf = "a + b + c",
		implicant = "c",
		valuations = {
			@Valuation(clause = 'a', valuation = false),
			@Valuation(clause = 'b', valuation = false),
			@Valuation(clause = 'c', valuation = true)
		}
	)
	@Test
	public void test3() {
		assertTrue(predicate14(1, 2, 3, 1, 2, 4));
	}

	@NearFalsePoint(
		predicate = "a + b + c",
		dnf = "a + b + c",
		implicant = "a",
		clause = 'a',
		valuations = {
			@Valuation(clause = 'a', valuation = false),
			@Valuation(clause = 'b', valuation = false),
			@Valuation(clause = 'c', valuation = false)
		}
	)
	@NearFalsePoint(
		predicate = "a + b + c",
		dnf = "a + b + c",
		implicant = "b",
		clause = 'b',
		valuations = {
			@Valuation(clause = 'a', valuation = false),
			@Valuation(clause = 'b', valuation = false),
			@Valuation(clause = 'c', valuation = false)
		}
	)
	@NearFalsePoint(
		predicate = "a + b + c",
		dnf = "a + b + c",
		implicant = "c",
		clause = 'c',
		valuations = {
			@Valuation(clause = 'a', valuation = false),
			@Valuation(clause = 'b', valuation = false),
			@Valuation(clause = 'c', valuation = false)
		}
	)
	@Test
	public void test4() {
		assertFalse(predicate14(1, 2, 3, 1, 2, 3));
	}

	/**
	 * Predicate: d + e
	 * Order d, e
	 * d:
	 *  CC : one test from {TT, TF} and one test from {FT, FF}
	 *  CACC : e should be false -> {TF, FF}
	 * e:
	 * 	CC : one test from {TT, FT} and one test from {TF, FF}
	 *	CACC : d should be false -> {FT, FF}
	 *
	 * CC :   {TT, FF}
	 * CACC : {FT, TF, FF}
	 */
	public boolean predicate15(double t1A, double t1B, double t1C) {
		assert t1A < t1B && t1B < t1C;
		return t1A < 0 || t1A + t1B < t1C;
	}

	@ClauseCoverage(
		predicate = "d + e",
		valuations = {
			@Valuation(clause = 'd', valuation = true),
			@Valuation(clause = 'e', valuation = true)
		}
	)
	@Test
	public void test5() {
		assertTrue(predicate15(-1, 2, 4));
	}

	@CACC(
		predicate = "d + e",
		majorClause = 'e',
		valuations = {
			@Valuation(clause = 'd', valuation = false),
			@Valuation(clause = 'e', valuation = true)
		},
		predicateValue = true
	)
	@Test
	public void test6() {
		assertTrue(predicate15(1, 2, 4));
	}

	@CACC(
		predicate = "d + e",
		majorClause = 'd',
		valuations = {
			@Valuation(clause = 'd', valuation = true),
			@Valuation(clause = 'e', valuation = false)
		},
		predicateValue = true
	)
	@Test
	public void test7() {
		// Infeasible
	}

	@ClauseCoverage(
		predicate = "d + e",
		valuations = {
			@Valuation(clause = 'd', valuation = false),
			@Valuation(clause = 'e', valuation = false)
		}
	)
	@CACC(
		predicate = "d + e",
		majorClause = 'd',
		valuations = {
			@Valuation(clause = 'd', valuation = false),
			@Valuation(clause = 'e', valuation = false)
		},
		predicateValue = false
	)
	@CACC(
		predicate = "d + e",
		majorClause = 'e',
		valuations = {
			@Valuation(clause = 'd', valuation = false),
			@Valuation(clause = 'e', valuation = false)
		},
		predicateValue = false
	)
	@Test
	public void test8() {
		assertFalse(predicate15(3, 4, 5));
	}

	@Test
	public void sampleTest() {
		Triangle t1 = new Triangle(2, 3, 7);
		Triangle t2 = new Triangle(7, 2, 3);
		boolean areCongruent = TriCongruence.areCongruent(t1, t2);
		log.debug("Triangles identified as '{}'.", areCongruent ? "Congruent" : "Not Congruent");
		Assertions.assertFalse(areCongruent);
	}

	/**
	 * Consider predicate: ab + cd
	 * First we find CUTPNFP
	 * Implicants: {ab, cd}
	 * ab:
	 * 	UTP: {TTFT}
	 * 	NFP:
	 * 	  a: {FTFT}
	 * 	  b: {TFFT}
	 * cd:
	 * 	UTP: {FTTT}
	 * 	NFP:
	 * 	  c: {FTFT}
	 * 	  d: {FTTF}
	 *
	 * CUTPNFP: {TTFT, FTTT, FTFT, TFFT, FTTF}
	 *
	 * Now we find UTP
	 * We should also consider ~f:
	 * 	 ~f = ~a~c + ~a~d + ~b~c + ~b~d
	 * Implicants: {ab, cd, ~a~c, ~a~d, ~b~c, ~b~d}
	 *
	 * We have 6 implicants, so we need 6 tests for UTPC but we have only 5 tests in CUTPNFP, so it can't subsume UTPC.
	 */
	private static boolean questionTwo(boolean a, boolean b, boolean c, boolean d) {
		boolean predicate = false;
		predicate = (a && b) || (c && d);
		return predicate;
	}
}
